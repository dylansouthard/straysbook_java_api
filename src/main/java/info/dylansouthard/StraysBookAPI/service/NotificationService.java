package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

//    public PaginatedResponseDTO<NotificationDTO> getNotifications(Long userId, List<Long> animalIds, LocalDate lastChecked, Integer page, Integer pageSize) {
//        int pageNo = page == null ? 0 : page;
//        int resNumber = pageSize == null ? PaginationConsts. : pageSize;
//        Page<NotificationDTO> notificationDTOPage = notificationRepository.findNotifications(animalIds, lastChecked, userId, )
//    }
}
