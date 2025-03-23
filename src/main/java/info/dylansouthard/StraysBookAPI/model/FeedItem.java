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
@Table(name = "feed_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"animals", "careEvent"})
public class FeedItem extends UserRegisteredDBEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedItemType type;

    @ManyToMany
    @JoinTable(
            name = "feed_item_animals",
            joinColumns = @JoinColumn(name = "feed_item_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private Set<Animal> animals = new HashSet<>();

    @OneToOne(mappedBy="feedItem")
    private CareEvent careEvent;

    @Column
    private String newValue;

    public void addAnimal(Animal animal) {
        animals.add(animal);
        if (!animal.getAssociatedFeedItems().contains(this)) {
            animal.getAssociatedFeedItems().add(this);
        }
    }

    @Transactional
    public void removeAnimal(Animal animal) {
        this.animals.removeIf(a -> a.getId().equals(animal.getId()));
        animal.getAssociatedFeedItems().removeIf(f -> f.getId().equals(this.getId()));
    }

    @PreRemove
    private void cleanUpRelationships()  {
        for (Animal animal : animals) {
            animal.getAssociatedFeedItems().remove(this);
        }
    }

    public FeedItem(FeedItemType type, List<Animal> animals) {
        this.type = type;
        for (Animal animal : animals) {
            addAnimal(animal);
        }
    }

    public FeedItem(List<Animal> animals, CareEvent careEvent, User registeredBy){

        super(registeredBy);
        this.type = FeedItemType.CARE_EVENT;
        for (Animal animal : animals) {
            addAnimal(animal);
        }
        this.careEvent = careEvent;
    }
}
