package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.FeedItem;
import info.dylansouthard.StraysBookAPI.model.enums.ConditionType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FeedItemRepositoryIT extends RepositoryIT {

    @Test
    @Transactional
    public void When_SavingFeedItem_Expect_FeedItemIsRetrievable() {
        FeedItem feedItem = constructValidConditionUpdateFeedItem();
        feedItem.setNewValue(ConditionType.UNDERFED.name());
        FeedItem savedFeedItem = feedItemRepository.save(feedItem);

        Optional<FeedItem> fetchedFeedItem = feedItemRepository.findById(savedFeedItem.getId());
        assertAll("FeedItem Save Assertions",
                ()->assertTrue(fetchedFeedItem.isPresent(), "Saved FeedItem should be retrievable."),
                ()->assertEquals(ConditionType.UNDERFED.name(), fetchedFeedItem.get().getNewValue())

        );
    }

    @Test
    @Transactional
    public void When_SavingInvalidFeedItem_Expect_ThrowException() {
        FeedItem feedItem = new FeedItem();
        assertThrows(DataIntegrityViolationException.class, () -> feedItemRepository.save(feedItem), "Saving invalid FeedItem should throw an error");
    }


    @Test
    @Transactional
    public void When_UpdatingFeedItem_Expect_FeedItemIsUpdated() {
        FeedItem feedItem = feedItemRepository.save(constructValidConditionUpdateFeedItem());

        feedItem.setNotes("test");

        feedItemRepository.save(feedItem);

        FeedItem fetchedFeedItem = feedItemRepository.findById(feedItem.getId()).get();

        assertEquals("test", fetchedFeedItem.getNotes(), "FeedItem should be updated");
    }

    @Test
    @Transactional
    public void When_AddingAnimalsToFeedItem_Expect_FeedItemIsAdded() {
        FeedItem feedItem = feedItemRepository.save(constructValidConditionUpdateFeedItem());
        FeedItem fetchedFeedItem = feedItemRepository.findById(feedItem.getId()).get();
        Animal fetchedAnimal = animalRepository.findById(feedItem.getAnimals().stream().findFirst().get().getId()).orElseThrow();

        assertAll("Feed Item animal relationship assertions",
                ()->assertEquals(2, fetchedFeedItem.getAnimals().size(), "Animals should be saved to feed item"),
                ()-> assertEquals(1, fetchedAnimal.getAssociatedFeedItems().size(), "Feed Item should be saved to animal")
                );
    }

    @Test
    @Transactional
    public void When_RemovingAnimalsToFeedItem_Expect_FeedItemIsRemoved() {
        FeedItem feedItem = feedItemRepository.save(constructValidConditionUpdateFeedItem());
        Animal animal = feedItem.getAnimals().stream().findFirst().get();
        feedItem.removeAnimal(animal);
        assertEquals(1, feedItem.getAnimals().size(), "Animals should be removed from feed item before saving");
        feedItemRepository.saveAndFlush(feedItem);

        FeedItem fetchedFeedItem = feedItemRepository.findById(feedItem.getId()).get();
        Animal fetchedAnimal = animalRepository.findById(animal.getId()).orElseThrow();

        assertAll("Feed Item animal relationship assertions",
                ()->assertEquals(1, fetchedFeedItem.getAnimals().size(), "Animals should be removed from feed item"),
                ()-> assertEquals(0, fetchedAnimal.getAssociatedFeedItems().size(), "Feed Item should be removed from animal")
        );
    }
}
