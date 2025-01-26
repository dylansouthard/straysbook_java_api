package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.enums.VaccinationType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name="vaccinations")
@NoArgsConstructor
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VaccinationType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_event_id", referencedColumnName = "id")
    private CareEvent careEvent;
}
