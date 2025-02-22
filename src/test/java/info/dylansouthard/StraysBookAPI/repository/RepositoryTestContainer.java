package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.BaseTestContainer;
import info.dylansouthard.StraysBookAPI.model.Animal;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryTestContainer extends BaseTestContainer {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected AnimalRepository animalRepository;

    @Autowired
    protected AuthTokenRepository authTokenRepository;

    @Autowired
    protected CareEventRepository careEventRepository;

    @Autowired
    protected SterilizationStatusRepository sterilizationStatusRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected VaccinationRepository vaccinationRepository;

    //MOCK ENTITIES
    protected Animal validAnimal;
    protected User validUser;

    @BeforeEach
    public void setUp() {
        this.validUser = new User("Bing Bong", "bing@bong.com");
        this.validAnimal = new Animal(AnimalType.CAT, SexType.UNKNOWN, "Amelie");
    }
}
