package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.model.Notification;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.friendo.Litter;
import info.dylansouthard.StraysBookAPI.model.user.User;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class RepositoryIT extends BaseDBTest {



    //MOCK ENTITIES
    protected Animal validAnimal;
    protected User validUser;

    List<Animal> constructValidAnimals() {
        Animal animal1 = new Animal(AnimalType.CAT, SexType.FEMALE, "SabiSabi");
        Animal animal2 = new Animal(AnimalType.CAT, SexType.FEMALE, "Ame-chan");
        return List.of(animal1, animal2);
    }

    List<Animal> constructAndSaveValidAnimals() {
        List<Animal> animals = constructValidAnimals();
        for (Animal animal : animals) {animalRepository.saveAndFlush(animal);}
        return animals;
    }

    protected Notification constructValidConditionUpdateFeedItem() {
        List<Animal> animals = constructAndSaveValidAnimals();
        return new Notification(FeedItemType.CONDITION_UPDATE, animals);
    }


    protected Litter constructValidLitter() {
        return new Litter(AnimalType.CAT, "New Litter", constructValidAnimals());
    }

    @BeforeEach
    public void setUp() {
        this.validUser = new User("Bing Bong", "bing@bong.com");
        this.validAnimal = new Animal(AnimalType.CAT, SexType.UNKNOWN, "Amelie");
    }
}
