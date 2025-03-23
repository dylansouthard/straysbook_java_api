package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.FeedItemDTO;
import info.dylansouthard.StraysBookAPI.model.FeedItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedItemMapper {

    FeedItemDTO toFeedItemDTO(FeedItem feedItem);
}
