package info.dylansouthard.StraysBookAPI;

import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.notification.Notification;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.*;
import info.dylansouthard.StraysBookAPI.testutils.NotificationLoader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

    protected <T extends Notification> List<T> constructAndSaveDummyNotifications(List<Animal> animals, User registeredBy, Class<T> clazz) throws IOException {
        if (animals.isEmpty()) return Collections.emptyList();

        List<T> notifications = NotificationLoader.loadDummyNotifications(clazz);

        for (int i = 0; i < notifications.size(); i++) {
            T notification = notifications.get(i);
            notification.setRegisteredBy(registeredBy);
            notification.setCreatedAt(LocalDateTime.now().minusDays(i));

            // Animal assignment logic
            if (animals.size() == 1) {
                notification.addAnimal(animals.get(0));
            } else {
                if (i % 2 == 0 || i % 3 == 0) {

                    notification.addAnimal(animals.get(0));
                }
                if (i % 2 != 0 || i % 3 == 0) {
                    notification.addAnimal(animals.get(1));
                }
            }
        }

        return notificationRepository.saveAll(notifications);
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
