package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="care_events")
@NoArgsConstructor
public class CareEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CareEventType type;

    @Column
    private Long associatedId;

    @Column
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime date; // Represents the actual event date

//    @PrePersist
//    private void setDefaultDate() {
//        if (date == null) {
//            date = super.getCreatedAt(); // Default to createdAt if date is not set
//        }
//    }

    @ManyToMany
    private List<Animal> animals;

    //ADD created by related to user

    //ADD litter related litter
}
