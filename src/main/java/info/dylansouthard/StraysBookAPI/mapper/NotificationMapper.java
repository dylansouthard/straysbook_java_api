package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.NotificationDTO;
import info.dylansouthard.StraysBookAPI.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDTO toFeedItemDTO(Notification notification);
}
