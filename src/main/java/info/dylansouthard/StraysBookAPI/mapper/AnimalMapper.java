package info.dylansouthard.StraysBookAPI.mapper;

import info.dylansouthard.StraysBookAPI.dto.ConditionDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryMinDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.CreateAnimalDTO;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {ConditionMapper.class, StatusMapper.class, UserMapper.class, VaccinationMapper.class})
public interface AnimalMapper {
    @Mapping(target="friendoType", expression ="java(info.dylansouthard.StraysBookAPI.model.enums.FriendoType.ANIMAL)")
    AnimalSummaryDTO toAnimalSummaryDTO(Animal animal);

    @Mapping(target = "friendoType", expression = "java(info.dylansouthard.StraysBookAPI.model.enums.FriendoType.ANIMAL)")
    @Mapping(source = ".", target = "condition", qualifiedByName = "mapCondition")
    @Mapping(source = ".", target = "status", qualifiedByName = "mapStatus")
    @Mapping(source = "registeredBy", target = "registeredBy", qualifiedByName = "toUserSummaryMinDTO")
    AnimalDTO toAnimalDTO(Animal animal);

    AnimalSummaryMinDTO toAnimalSummaryMinDTO(Animal animal);

    Animal fromCreateAnimalDTO(CreateAnimalDTO dto);

    @AfterMapping
    default void mapConditionDetails(CreateAnimalDTO dto, @MappingTarget Animal animal) {
        ConditionDTO condition = dto.getCondition();
        if (condition != null) {
            animal.setConditions(condition.getTypes());
            animal.setCriticalCondition(condition.getCritical());
            animal.setConditionRating(condition.getRating());
            animal.setConditionNotes(condition.getNotes());
        }
    }
}
