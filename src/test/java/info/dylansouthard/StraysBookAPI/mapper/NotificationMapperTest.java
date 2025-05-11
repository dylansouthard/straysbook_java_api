package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.NotificationDTO;
import info.dylansouthard.StraysBookAPI.model.Notification;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static info.dylansouthard.StraysBookAPI.config.DummyTestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationMapperTest {
    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);


    private Notification createFeedItemWithUserAndAnimal(){
        Notification notification = new Notification();
        notification.setId(1L);
        Animal animal = createAnimal();
        notification.getAnimals().add(animal);
        User user = createUser();
        notification.setRegisteredBy(user);
        return notification;
    }

    @Test
    void When_MappingToFeedItemDTO_Then_ReturnFeedItemDTO() {

        Notification notification = createFeedItemWithUserAndAnimal();
        notification.setType(FeedItemType.PROFILE_UPDATE);
        NotificationDTO dto = notificationMapper.toFeedItemDTO(notification);


        assertAll("Feed Item DTO Assertions",
                ()->assertNotNull(dto, "NotificationDTO should not be null"),
                ()->assertEquals(1, dto.getAnimals().size(), "FeedItem animals count should be 1"),
                ()->assertNotNull(dto.getRegisteredBy(), "FeedItem RegisteredBy should not be null")
                );
    }

    @Test
    void When_MappingToCareEventFeedItemDTO_Then_ReturnCareEventFeedItemDTO() {
        Notification notification = createFeedItemWithUserAndAnimal();
        notification.setType(FeedItemType.CARE_EVENT);
        notification.setCareEvent(createCareEvent());
        NotificationDTO dto = notificationMapper.toFeedItemDTO(notification);

        assertNotNull(dto.getCareEvent(), "FeedItem CareEvent should not be null");
    }


}
