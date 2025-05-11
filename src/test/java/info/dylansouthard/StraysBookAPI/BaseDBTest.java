package info.dylansouthard.StraysBookAPI;

import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class BaseDBTest extends BaseTestContainer{
    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected AnimalRepository animalRepository;

    @Autowired
    protected AuthTokenRepository authTokenRepository;

    @Autowired
    protected CareEventRepository careEventRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected LitterRepository litterRepository;

    @Autowired
    protected SterilizationStatusRepository sterilizationStatusRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected VaccinationRepository vaccinationRepository;


    protected CareEvent addAnimalAndSave(CareEvent event, Animal animal) {
        event.getAnimals().add(animal);
        return careEventRepository.saveAndFlush(event);
    }

    protected Animal createAnimalWithPrimaryCaretaker(User primaryCaretaker, User nonPrimaryCaretaker) {
        // Create Animal
        Animal animal = DummyTestData.createAnimal();
        animal.setRegisteredBy(primaryCaretaker);
        Animal savedAnimal = animalRepository.saveAndFlush(animal);

        // Add Care Events
        CareEvent event1 = addAnimalAndSave(new CareEvent(CareEventType.FED, LocalDateTime.now().minusDays(5), primaryCaretaker), savedAnimal);
        CareEvent event2 = addAnimalAndSave(new CareEvent(CareEventType.VACCINATED, LocalDateTime.now().minusDays(3), primaryCaretaker), savedAnimal);
        CareEvent event3 = addAnimalAndSave(new CareEvent(CareEventType.STERILIZED, LocalDateTime.now().minusDays(1), nonPrimaryCaretaker), savedAnimal);

        return animal;
    }
}
