package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.base.UserRegisteredDBEntity;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.ConditionType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.enums.StatusType;
import info.dylansouthard.StraysBookAPI.model.shared.GeoSchema;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="animals")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"usersWatching", "careEvents"})
public class Animal extends UserRegisteredDBEntity {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType statusType = StatusType.STRAY;

    @Column
    private String statusNotes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SterilizationStatus sterilizationStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vaccination> vaccinations = new HashSet<>();

    @ManyToMany(mappedBy = "animals")
    private Set<CareEvent> careEvents = new HashSet<>();

    @ManyToMany(mappedBy = "watchedAnimals")
    private Set<User> usersWatching = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void initializeSterilizationStatus() {
        if (sterilizationStatus == null) {
            sterilizationStatus = new SterilizationStatus();
        }
    }

    @PreRemove
    private void cleanUpRelationships() {
        for (User user : usersWatching) {
            user.getWatchedAnimals().remove(this);
        }

    }

    public Animal(AnimalType type, SexType sex, String name) {
        this.type = type;
        this.sex = sex;
        this.name = name;
    }
}
