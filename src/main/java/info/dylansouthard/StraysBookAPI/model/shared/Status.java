package info.dylansouthard.StraysBookAPI.model.shared;

import info.dylansouthard.StraysBookAPI.model.enums.StatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Status {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType type = StatusType.STRAY;

    @Column
    private String notes;

//    @OneToOne
//    private User registeredBy;
}
