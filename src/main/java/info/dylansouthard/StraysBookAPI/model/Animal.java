package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.ConditionType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.shared.GeoSchema;
import info.dylansouthard.StraysBookAPI.model.shared.Status;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="animals")
@NoArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="animal_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private AnimalType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SexType sex = SexType.UNKNOWN;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private LocalDate born;

    @Column
    private Boolean dangerous;

    @Column
    private Boolean hasCollar;

    @Column
    private String imgUrl;

    @Column
    private Boolean shouldAppear = true;

    @Column
    private LocalDateTime removeAt = null;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "animal_conditions", joinColumns = @JoinColumn(name = "animal_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "conditions")
    private List<ConditionType> conditions = new ArrayList<>();

    @Column
    private Boolean criticalCondition;

    @Column
    private int conditionRating;

    @Embedded
    private GeoSchema location;

    @Embedded
    private Status status = new Status();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SterilizationStatus sterilizationStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vaccination> vaccinations = new ArrayList<>();

    @ManyToMany
    private List<CareEvent> careEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "watchedAnimals")
    private List<User> usersWatching = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void initializeSterilizationStatus() {
        if (sterilizationStatus == null) {
            sterilizationStatus = new SterilizationStatus();
        }
    }

}
