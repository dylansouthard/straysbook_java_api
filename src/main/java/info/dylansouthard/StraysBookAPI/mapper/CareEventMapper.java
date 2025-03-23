package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.careEvent.CareEventDTO;
import info.dylansouthard.StraysBookAPI.dto.careEvent.CareEventSummaryDTO;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CareEventMapper {

    CareEventDTO toCareEventDTO(CareEvent careEvent);

    CareEventSummaryDTO toCareEventSummaryDTO(CareEvent careEvent);
}

