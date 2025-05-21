package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import jakarta.transaction.Transactional;

@Transactional
public class NotificationMapperTest extends BaseDBTest {
//    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);
//
//    private Notification createNotificationWithUserAndAnimal(){
//        Notification notification = new Notification();
//        notification.setId(1L);
//        Animal animal = createAnimal();
//        notification.getAnimals().add(animal);
//        User user = createUser();
//        notification.setRegisteredBy(user);
//        return notification;
//    }
//
//    @Test
//    void When_MappingToNotificationDTO_Then_ReturnNotificationDTO() {
//
//        Notification notification = createNotificationWithUserAndAnimal();
//        notification.setType(NotificationContentType.PROFILE_UPDATE);
//        NotificationDTO dto = notificationMapper.toNotificationDTO(notification);
//
//
//        assertAll("Notification DTO Assertions",
//                ()->assertNotNull(dto, "NotificationDTO should not be null"),
//                ()->assertEquals(1, dto.getAnimals().size(), "Notification animals count should be 1"),
//                ()->assertNotNull(dto.getRegisteredBy(), "Notification RegisteredBy should not be null")
//                );
//    }
//
//    @Test
//    void When_MappingToCareEventNotificationDTO_Then_ReturnCareEventNotificationDTO() {
//        Notification notification = createNotificationWithUserAndAnimal();
//        notification.setType(NotificationContentType.CARE_EVENT);
//        notification.setCareEvent(createCareEvent());
//        NotificationDTO dto = notificationMapper.toNotificationDTO(notification);
//        assertNotNull(dto.getCareEvent(), "Notification CareEvent should not be null");
//    }
//
//    @Test
//    void shouldMapPageToPaginatedResponseDTO() {
//        // Create concrete notifications
//        Notification notification1 = new Notification();
//        notification1.setId(1L);
//        Notification notification2 = new Notification();
//        notification2.setId(2L);
//
//        // Create a list of notifications
//        List<Notification> notifications = List.of(notification1, notification2);
//
//        // Use PageRequest to correctly specify the page size
//        PageRequest pageRequest = PageRequest.of(0, 10); // First page, size 10
//
//        // Use the correct total number of elements and simulate a single page
//        Page<Notification> notificationPage = new PageImpl<>(notifications, pageRequest, notifications.size());
//
//        // Map to PaginatedResponseDTO using the mapper
//        PaginatedResponseDTO<NotificationDTO> response = notificationMapper.toPaginatedResponseDTO(notificationPage);
//
//        // Perform assertions
//        assertAll(
//                "Notification Mapper units",
//                () -> assertEquals(2, response.getContent().size(),
//                        "Expected number of notifications in the content does not match the actual size."),
//                () -> assertEquals(0, response.getPageNumber(),
//                        "Expected page number does not match the actual page number."),
//                () -> assertEquals(10, response.getPageSize(),
//                        "Expected page size does not match the actual page size."),
//                () -> assertEquals(2, response.getTotalElements(),
//                        "Expected total elements do not match the actual number of elements."),
//                () -> assertEquals(1, response.getTotalPages(),
//                        "Expected total pages do not match the actual total pages."),
//                () -> assertFalse(response.isHasNext(),
//                        "Expected no next page, but response indicates there is one."),
//                () -> assertFalse(response.isHasPrevious(),
//                        "Expected no previous page, but response indicates there is one.")
//        );
//    }


}
