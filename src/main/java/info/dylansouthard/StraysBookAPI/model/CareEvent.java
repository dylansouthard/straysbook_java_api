package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.base.UserRegisteredDBEntity;
import info.dylansouthard.StraysBookAPI.model.enums.CareEventType;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="care_events")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude={"animals"})
public class CareEvent extends UserRegisteredDBEntity {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CareEventType type;

    @Column
    private Long associatedId;

    @Column
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "care_event_animals", // Name of the join table
            joinColumns = @JoinColumn(name = "care_event_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id") // Foreign key for Animal
    )
    private Set<Animal> animals = new HashSet<>();

    public CareEvent(CareEventType type, LocalDateTime date, User registeredBy) {
        super(registeredBy);
        this.type = type;
        this.date = date;
        registeredBy.getCareEvents().add(this);
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
        animal.getCareEvents().add(this);
    }

    @PreRemove
    public void removeCareEventAssociations() {
        for (Animal animal : animals) {
            animal.getCareEvents().remove(this);
        }
    }



    //ADD litter related litter
}
