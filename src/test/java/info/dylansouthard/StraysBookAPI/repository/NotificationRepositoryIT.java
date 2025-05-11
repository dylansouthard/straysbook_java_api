package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.Notification;
import info.dylansouthard.StraysBookAPI.model.enums.ConditionType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRepositoryIT extends RepositoryIT {

    @Test
    @Transactional
    public void When_SavingFeedItem_Expect_FeedItemIsRetrievable() {
        Notification notification = constructValidConditionUpdateFeedItem();
        notification.setNewValue(ConditionType.UNDERFED.name());
        Notification savedNotification = notificationRepository.save(notification);

        Optional<Notification> fetchedFeedItem = notificationRepository.findById(savedNotification.getId());
        assertAll("FeedItem Save Assertions",
                ()->assertTrue(fetchedFeedItem.isPresent(), "Saved FeedItem should be retrievable."),
                ()->assertEquals(ConditionType.UNDERFED.name(), fetchedFeedItem.get().getNewValue())

        );
    }

    @Test
    @Transactional
    public void When_SavingInvalidFeedItem_Expect_ThrowException() {
        Notification notification = new Notification();
        assertThrows(DataIntegrityViolationException.class, () -> notificationRepository.save(notification), "Saving invalid FeedItem should throw an error");
    }


    @Test
    @Transactional
    public void When_UpdatingFeedItem_Expect_FeedItemIsUpdated() {
        Notification notification = notificationRepository.save(constructValidConditionUpdateFeedItem());

        notification.setNotes("test");

        notificationRepository.save(notification);

        Notification fetchedNotification = notificationRepository.findById(notification.getId()).get();

        assertEquals("test", fetchedNotification.getNotes(), "FeedItem should be updated");
    }

    @Test
    @Transactional
    public void When_AddingAnimalsToFeedItem_Expect_FeedItemIsAdded() {
        Notification notification = notificationRepository.save(constructValidConditionUpdateFeedItem());
        Notification fetchedNotification = notificationRepository.findById(notification.getId()).get();
        Animal fetchedAnimal = animalRepository.findById(notification.getAnimals().stream().findFirst().get().getId()).orElseThrow();

        assertAll("Feed Item animal relationship assertions",
                ()->assertEquals(2, fetchedNotification.getAnimals().size(), "Animals should be saved to notification"),
                ()-> assertEquals(1, fetchedAnimal.getAssociatedNotifications().size(), "Feed Item should be saved to animal")
                );
    }

    @Test
    @Transactional
    public void When_RemovingAnimalsToFeedItem_Expect_FeedItemIsRemoved() {
        Notification notification = notificationRepository.save(constructValidConditionUpdateFeedItem());
        Animal animal = notification.getAnimals().stream().findFirst().get();
        notification.removeAnimal(animal);
        assertEquals(1, notification.getAnimals().size(), "Animals should be removed from notification before saving");
        notificationRepository.saveAndFlush(notification);

        Notification fetchedNotification = notificationRepository.findById(notification.getId()).get();
        Animal fetchedAnimal = animalRepository.findById(animal.getId()).orElseThrow();

        assertAll("Feed Item animal relationship assertions",
                ()->assertEquals(1, fetchedNotification.getAnimals().size(), "Animals should be removed from notification"),
                ()-> assertEquals(0, fetchedAnimal.getAssociatedNotifications().size(), "Feed Item should be removed from animal")
        );
    }
}
