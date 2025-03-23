package info.dylansouthard.StraysBookAPI.dto.user;

import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivateDTO extends UserPublicDTO{
    @Schema(
            description = "Email address of the user",
            example = "jimbo@example.com",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
            description = "List of animals the user is watching. Empty if none.",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Set<AnimalSummaryDTO> watchedAnimals = new HashSet<>();
}
