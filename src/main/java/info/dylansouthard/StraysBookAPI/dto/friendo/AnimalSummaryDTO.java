package info.dylansouthard.StraysBookAPI.dto.friendo;

import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AnimalSummaryDTO extends FriendoSummaryDTO {
    @Schema(
            description = "Sex of the animal",
            example = "FEMALE",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SexType sex;
}
