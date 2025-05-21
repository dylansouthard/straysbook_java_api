package info.dylansouthard.StraysBookAPI.model.notification;

import info.dylansouthard.StraysBookAPI.model.enums.PriorityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("ALERT")
@Getter @Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AlertNotification extends Notification {
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private PriorityType priority = PriorityType.LOW;
}
