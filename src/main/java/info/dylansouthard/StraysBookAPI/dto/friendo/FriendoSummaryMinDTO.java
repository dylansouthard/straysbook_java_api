package info.dylansouthard.StraysBookAPI.dto.friendo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FriendoSummaryMinDTO {
    @Schema(
            description = "Unique ID of the animal/litter",
            example = "1",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "Name of the animal/litter",
            example = "Rebecca Anderson Clive",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Image URL of the animal/litter",
            example = "https://www.google.com/img.jpg",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            format = "uri"
    )
    private String imgUrl;

}
