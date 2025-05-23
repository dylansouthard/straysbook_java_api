package info.dylansouthard.StraysBookAPI.dto;

import info.dylansouthard.StraysBookAPI.dto.careEvent.CareEventSummaryDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryMinDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserSummaryMinDTO;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDTO {

    @Schema(
            description = "Unique ID of the notification",
            example = "123",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "integer"
    )
    private Long id;

    @Schema(
            description = "Type of notification (e.g., CARE_EVENT, STATUS_UPDATE, NOTE)",
            example = "CARE_EVENT",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private FeedItemType type;

    @Schema(
            description = "List of animals associated with this notification",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<AnimalSummaryMinDTO> animals = new ArrayList<>();

    @Schema(
            description = "Summary of the care event linked to this notification, if applicable",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private CareEventSummaryDTO careEvent;

    @Schema(
            description = "New value associated with this notification, such as a status change or update note",
            example = "Vaccinated",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String newValue;

    @Schema(
            description = "User who registered the notification",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UserSummaryMinDTO registeredBy;

    @Schema(
            description = "Timestamp when the notification was created, in ISO 8601 format",
            example = "2025-03-16T14:30:00",
            nullable = false,
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            format = "date-time"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Optional notes or comments about the notification",
            example = "Animal showed improvement after treatment.",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String notes;
}
