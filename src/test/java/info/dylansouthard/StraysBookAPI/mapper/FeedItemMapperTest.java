package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.FeedItemDTO;
import info.dylansouthard.StraysBookAPI.model.FeedItem;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static info.dylansouthard.StraysBookAPI.config.DummyTestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class FeedItemMapperTest {
    private final FeedItemMapper feedItemMapper = Mappers.getMapper(FeedItemMapper.class);


    private FeedItem createFeedItemWithUserAndAnimal(){
        FeedItem feedItem = new FeedItem();
        feedItem.setId(1L);
        Animal animal = createAnimal();
        feedItem.getAnimals().add(animal);
        User user = createUser();
        feedItem.setRegisteredBy(user);
        return feedItem;
    }

    @Test
    void When_MappingToFeedItemDTO_Then_ReturnFeedItemDTO() {

        FeedItem feedItem = createFeedItemWithUserAndAnimal();
        feedItem.setType(FeedItemType.PROFILE_UPDATE);
        FeedItemDTO dto = feedItemMapper.toFeedItemDTO(feedItem);


        assertAll("Feed Item DTO Assertions",
                ()->assertNotNull(dto, "FeedItemDTO should not be null"),
                ()->assertEquals(1, dto.getAnimals().size(), "FeedItem animals count should be 1"),
                ()->assertNotNull(dto.getRegisteredBy(), "FeedItem RegisteredBy should not be null")
                );
    }

    @Test
    void When_MappingToCareEventFeedItemDTO_Then_ReturnCareEventFeedItemDTO() {
        FeedItem feedItem = createFeedItemWithUserAndAnimal();
        feedItem.setType(FeedItemType.CARE_EVENT);
        feedItem.setCareEvent(createCareEvent());
        FeedItemDTO dto = feedItemMapper.toFeedItemDTO(feedItem);

        assertNotNull(dto.getCareEvent(), "FeedItem CareEvent should not be null");
    }


}
