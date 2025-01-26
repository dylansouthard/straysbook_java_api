package info.dylansouthard.StraysBookAPI.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sterilization_statuses")
@NoArgsConstructor
public class SterilizationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean sterilized;
}
