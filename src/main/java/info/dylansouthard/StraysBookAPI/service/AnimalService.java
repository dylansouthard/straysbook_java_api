package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.CreateAnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.UpdateAnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserSummaryMinDTO;
import info.dylansouthard.StraysBookAPI.errors.AppException;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.mapper.AnimalMapper;
import info.dylansouthard.StraysBookAPI.mapper.CareEventMapper;
import info.dylansouthard.StraysBookAPI.mapper.UserMapper;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.AnimalRepository;
import info.dylansouthard.StraysBookAPI.repository.UserRepository;
import info.dylansouthard.StraysBookAPI.rules.enums.AccessLevel;
import info.dylansouthard.StraysBookAPI.rules.update.UpdateRule;
import info.dylansouthard.StraysBookAPI.rules.update.UpdateRuleLoader;
import info.dylansouthard.StraysBookAPI.util.updaters.AnimalUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static info.dylansouthard.StraysBookAPI.util.CareEventWeightUtil.weightCareEvent;


@Service
public class AnimalService {
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CareEventService careEventService;
    @Autowired
    private CareEventMapper careEventMapper;


    public AnimalDTO createAnimal(CreateAnimalDTO createDTO, User user){
        if (createDTO == null) {
            throw ErrorFactory.invalidCreate();
        }
        try {
            Animal animal = animalMapper.fromCreateAnimalDTO(createDTO);
            animal.setRegisteredBy(user);
            Animal savedAnimal = animalRepository.save(animal);
            user.addWatchedAnimal(animal);
            userRepository.save(user);
            return animalMapper.toAnimalDTO(savedAnimal);
        } catch (DataIntegrityViolationException e) {
            throw ErrorFactory.invalidCreate();
        } catch (Exception e) {
            throw ErrorFactory.internalServerError();
        }
    }

    public List<AnimalSummaryDTO> getAnimalsInArea(Double latitude, Double longitude, Double radius) {
        if (latitude == null || longitude == null) throw ErrorFactory.invalidCoordinates();

        List<Animal> animals = animalRepository.findByLocation(latitude, longitude, radius);

        return animals.stream().map(animalMapper::toAnimalSummaryDTO).toList();
    }


    public AnimalDTO fetchAnimalDetails(Long id) {
       Animal animal = fetchAnimalById(id);

        try {
            AnimalDTO animalDTO = animalMapper.toAnimalDTO(animal);
            List<CareEvent> recentCareEvents = careEventService.getAllRecentCareEventsByAnimalId(id);
            UserSummaryMinDTO primaryCareTaker = getPrimaryCaretaker(recentCareEvents, animal);

            animalDTO.setRecentCareEvents(recentCareEvents.stream().map(careEventMapper::toCareEventSummaryDTO).toList());
            animalDTO.setPrimaryCaretaker(primaryCareTaker);

            return animalDTO;

        } catch (Exception e) {
            throw ErrorFactory.internalServerError();
        }
    }

    public AnimalDTO updateAnimal(Long animalId, UpdateAnimalDTO updateDTO, User user) {
        if (updateDTO == null) throw ErrorFactory.invalidParams();

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(ErrorFactory::animalNotFound);

        Map<String, Object> updates = updateDTO.getUpdates();

        UserSummaryMinDTO primaryCaretaker = getPrimaryCaretaker(animal);

        boolean canUpdateAsPrimaryCaretaker = canUpdateAsPrimaryCaretaker(user, primaryCaretaker);

        int numAuthorizedUpdates = 0;

        try {
            Map<String, UpdateRule> rules = UpdateRuleLoader.getAnimalRules();
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String key = entry.getKey();
                Object newValue = entry.getValue();

                UpdateRule rule = rules.get(key);
                if (rule == null) continue;

                Object currentValue = AnimalUpdater.getCurrentValue(animal, key);

                AccessLevel accessLevel = rule.getAccess();

                boolean canUpdate = accessLevel == AccessLevel.PUBLIC
                        || canUpdateAsPrimaryCaretaker
                        || (accessLevel == AccessLevel.CONDITIONAL
                        && Objects.equals(String.valueOf(currentValue), String.valueOf(rule.getCondition())));

                    if (canUpdate) {
                        numAuthorizedUpdates++;
                        AnimalUpdater.applyUpdate(animal, key, newValue);
                    }
            }

            if (numAuthorizedUpdates == 0) throw ErrorFactory.authForbidden();
           Animal updatedAnimal = animalRepository.save(animal);
            AnimalDTO animalDTO = animalMapper.toAnimalDTO(updatedAnimal);
            animalDTO.setPrimaryCaretaker(primaryCaretaker);
            return animalDTO;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ErrorFactory.internalServerError();
        }
    }

    public void deleteAnimal(Long animalId, User user) {
        Animal animal = fetchAnimalById(animalId);
        UserSummaryMinDTO primaryCaretaker = getPrimaryCaretaker(animal);
        if (!canUpdateAsPrimaryCaretaker(user, primaryCaretaker)) throw ErrorFactory.authForbidden();
        try {
            animal.setShouldAppear(false);
            animal.setRemoveAt(LocalDateTime.now().plusDays(30));
            animalRepository.save(animal);
        } catch (Exception e) {
            throw ErrorFactory.internalServerError();
        }
    }

    public UserSummaryMinDTO getPrimaryCaretaker(List<CareEvent> events, Animal animal) {
        if (events.isEmpty()) return null;

        Map<User, Integer> caretakerScores = new HashMap<>();

        //  New animal bonus
        if (animal.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)) && animal.getRegisteredBy() != null) {
            caretakerScores.merge(animal.getRegisteredBy(), 5, Integer::sum);
        }

        // Score all care events
        for (CareEvent event : events) {
            User user = event.getRegisteredBy();
            if (user == null) continue;
            int score = weightCareEvent(event.getType());
            caretakerScores.merge(user, score, Integer::sum);
        }

        //  Find highest score
        int maxScore = caretakerScores.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        //  Find users with max score
        List<User> topUsers = caretakerScores.entrySet().stream()
                .filter(entry -> entry.getValue() == maxScore)
                .map(Map.Entry::getKey)
                .toList();

        if (topUsers.size() == 1) {
            return userMapper.toUserSummaryMinDTO(topUsers.get(0));
        }

        // Tie-breaker: latest care event
        User latestUser = events.stream()
                .filter(e -> e.getRegisteredBy() != null && topUsers.contains(e.getRegisteredBy()))
                .sorted(Comparator.comparing(CareEvent::getDate).reversed())  // Most recent first
                .map(CareEvent::getRegisteredBy)
                .findFirst()
                .orElse(null);

        if (latestUser != null) {
            return userMapper.toUserSummaryMinDTO(latestUser);
        }

        return null;
    }

    private UserSummaryMinDTO getPrimaryCaretaker(Animal animal) {
        List<CareEvent> recentCareEvents = careEventService.getAllRecentCareEventsByAnimalId(animal.getId());
        return getPrimaryCaretaker(recentCareEvents, animal);
    }

    private Boolean canUpdateAsPrimaryCaretaker(User user, UserSummaryMinDTO primaryCaretaker) {
        return primaryCaretaker == null || primaryCaretaker.getId().equals(user.getId());
    }



    private Animal fetchAnimalById(Long id) {
        if (id == null) {
            throw ErrorFactory.invalidParams();
        }
        return animalRepository.findById(id)
                .orElseThrow(ErrorFactory::animalNotFound);
    }

}
