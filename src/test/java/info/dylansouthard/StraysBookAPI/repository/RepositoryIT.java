package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.BaseTestContainer;
import info.dylansouthard.StraysBookAPI.model.FeedItem;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.FeedItemType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.friendo.Litter;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RepositoryIT extends BaseTestContainer {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected AnimalRepository animalRepository;

    @Autowired
    protected AuthTokenRepository authTokenRepository;

    @Autowired
    protected CareEventRepository careEventRepository;

    @Autowired
    protected FeedItemRepository feedItemRepository;

    @Autowired
    protected LitterRepository litterRepository;

    @Autowired
    protected SterilizationStatusRepository sterilizationStatusRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected VaccinationRepository vaccinationRepository;

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

    protected FeedItem constructValidConditionUpdateFeedItem() {
        List<Animal> animals = constructAndSaveValidAnimals();
        return new FeedItem(FeedItemType.CONDITION_UPDATE, animals);
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
