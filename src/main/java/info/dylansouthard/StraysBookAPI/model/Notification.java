package info.dylansouthard.StraysBookAPI.model;

import info.dylansouthard.StraysBookAPI.model.base.UserRegisteredDBEntity;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"animals", "careEvent"})
public class Notification extends UserRegisteredDBEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedItemType type;

    @ManyToMany
    @JoinTable(
            name = "notification_animals",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private Set<Animal> animals = new HashSet<>();

    @OneToOne(mappedBy="notification")
    private CareEvent careEvent;

    @Column
    private String newValue;

    public void addAnimal(Animal animal) {
        animals.add(animal);
        if (!animal.getAssociatedNotifications().contains(this)) {
            animal.getAssociatedNotifications().add(this);
        }
    }

    @Transactional
    public void removeAnimal(Animal animal) {
        this.animals.removeIf(a -> a.getId().equals(animal.getId()));
        animal.getAssociatedNotifications().removeIf(f -> f.getId().equals(this.getId()));
    }

    @PreRemove
    private void cleanUpRelationships()  {
        for (Animal animal : animals) {
            animal.getAssociatedNotifications().remove(this);
        }
    }

    public Notification(FeedItemType type, List<Animal> animals) {
        this.type = type;
        for (Animal animal : animals) {
            addAnimal(animal);
        }
    }

    public Notification(List<Animal> animals, CareEvent careEvent, User registeredBy){

        super(registeredBy);
        this.type = FeedItemType.CARE_EVENT;
        for (Animal animal : animals) {
            addAnimal(animal);
        }
        this.careEvent = careEvent;
    }
}
