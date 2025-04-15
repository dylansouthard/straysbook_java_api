package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.cases.AnimalUpdateTestCase;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.AnimalSummaryDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.CreateAnimalDTO;
import info.dylansouthard.StraysBookAPI.dto.friendo.UpdateAnimalDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.mapper.AnimalMapper;
import info.dylansouthard.StraysBookAPI.model.enums.AnimalType;
import info.dylansouthard.StraysBookAPI.model.enums.SexType;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.shared.GeoSchema;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.testutils.ExceptionAssertionRunner;
import info.dylansouthard.StraysBookAPI.util.updaters.AnimalUpdater;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Map;

import static info.dylansouthard.StraysBookAPI.config.DummyTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@Transactional
public class AnimalServiceIT extends BaseDBTest {
    @Autowired
    AnimalService animalService;

    @MockitoSpyBean
    private AnimalMapper spyMapper;

    @Test
    public void When_ValidCreateAnimalDTO_Expect_AnimalIsCreatedAndSaved() {

       User user = userRepository.save(DummyTestData.createUser());

       CreateAnimalDTO dto = generateCreateAnimalDTO();

        // Act
        AnimalDTO result = animalService.createAnimal(dto, user);

        // Assert
        assertAll("AnimalDTO validation",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(defaultAnimalName, result.getName(), "Animal name should be " + defaultAnimalName),
                () -> assertEquals(AnimalType.CAT, result.getType(), "Animal type should be CAT")
        );

        //Fetch from DB
        Animal fetchedAnimal = animalRepository.findById(result.getId()).orElse(null);
        assertAll("AnimalDTO saved validation",
                () -> assertNotNull(fetchedAnimal, "Result should not be null"),
                () -> assertEquals(defaultAnimalName, fetchedAnimal.getName(), "Animal name should be " + defaultAnimalName),
                () -> assertEquals(AnimalType.CAT, fetchedAnimal.getType(), "Animal type should be CAT")
        );
    }


    @Test
    public void When_InvalidCreateAnimalDTO_Expect_ThrowsError() {
        User user = userRepository.save(DummyTestData.createUser());

        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> animalService.createAnimal(null, user),
                ErrorFactory.invalidCreate(),
                "Validate AppException details"
        );

        CreateAnimalDTO dto = new CreateAnimalDTO();
        dto.setName(null);
        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> animalService.createAnimal(dto, user),
                ErrorFactory.invalidCreate(),
                "Validate Invalid AppException details"
        );

    }

    @Test
    public void When_MapperThrowsException_Expect_InternalServerError() {
        User user = userRepository.save(DummyTestData.createUser());

        CreateAnimalDTO dto = generateCreateAnimalDTO();

        // Force mapper to throw an exception
        doThrow(new RuntimeException("Boom")).when(spyMapper).fromCreateAnimalDTO(any(CreateAnimalDTO.class));

        ExceptionAssertionRunner.assertThrowsExceptionOfType(() -> animalService.createAnimal(dto, user), ErrorFactory.internalServerError(), "Internal server error check");
    }

    @Test
    public void When_AnimalDetailsFetched_Expect_AnimalIsRetrieved() {
        User user = userRepository.save(DummyTestData.createUser());

        Animal animal = DummyTestData.createAnimal();
        animal.setRegisteredBy(user);
        Animal savedAnimal = animalRepository.save(animal);

        AnimalDTO dto = animalService.fetchAnimalDetails(savedAnimal.getId());

        assertAll("AnimalDTO Assertions",
                () -> assertNotNull(dto, "AnimalDTO should not be null"),
                () -> assertEquals(savedAnimal.getId(), dto.getId(), "Animal ID should match"),
                () -> assertEquals(savedAnimal.getName(), dto.getName(), "Animal name should match"),
                () -> assertNotNull(dto.getRegisteredBy(), "RegisteredBy should not be null"),
                () -> assertEquals(user.getDisplayName(), dto.getRegisteredBy().getDisplayName(), "Display name should match"),
                () -> assertNotNull(dto.getRecentCareEvents(), "Recent care events list should not be null"),
                () -> assertTrue(dto.getRecentCareEvents().isEmpty(), "Recent care events should be empty"),
                () -> assertNull(dto.getPrimaryCaretaker(), "Primary caretaker should be null with no care events")
        );
    }

    @Test
    @Transactional
    public void When_AnimalHasCareEvents_Expect_PrimaryCaretakerAndEventsSet() {
        // Create Users
        User userA = userRepository.save(DummyTestData.createUser());
        User userB = userRepository.save(new User("User B", "abc@abc.com"));

        Animal savedAnimal = createAnimalWithPrimaryCaretaker(userA, userB);
        // Fetch DTO
        AnimalDTO dto = animalService.fetchAnimalDetails(savedAnimal.getId());

        assertAll("AnimalDTO with Care Events",
                () -> assertNotNull(dto, "AnimalDTO should not be null"),
                () -> assertEquals(savedAnimal.getId(), dto.getId(), "Animal ID should match"),
                () -> assertNotNull(dto.getRecentCareEvents(), "Recent care events should not be null"),
                () -> assertEquals(3, dto.getRecentCareEvents().size(), "Should have 3 care events"),
                () -> assertNotNull(dto.getPrimaryCaretaker(), "Primary caretaker should not be null"),
                () -> assertEquals(userA.getDisplayName(), dto.getPrimaryCaretaker().getDisplayName(), "UserB should be primary caretaker")
        );
    }

    @Test
    @Transactional
    public void When_FindingAnimalsInArea_Expect_CorrectAnimalFound() {
        // Given
        Animal inRangeAnimal = new Animal(AnimalType.CAT, SexType.FEMALE, "アメリ", new GeoSchema(34.7376, 135.3415)); // Within radius
        Animal outOfRangeAnimal = new Animal(AnimalType.DOG, SexType.MALE, "Borky", new GeoSchema(14.0000, 136.0000)); // Outside radius

        animalRepository.save(inRangeAnimal);
        animalRepository.save(outOfRangeAnimal);

        // When
        List<AnimalSummaryDTO> foundAnimals = animalService.getAnimalsInArea(34.7385, 135.3415, 1000.0);  // 1000m radius

        // Then
        assertAll("Validate only in-range animal is found",
                () -> assertEquals(1, foundAnimals.size(), "Only one animal should be found in the area"),
                () -> assertEquals("アメリ", foundAnimals.get(0).getName(), "Found animal should be アメリ")
        );
    }

    @Test
    public void When_FindingInvalidAnimal_Expect_NotFoundError() {
        long invalidId = 9999L;
        ExceptionAssertionRunner.assertThrowsExceptionOfType(()->animalService.fetchAnimalDetails(invalidId), ErrorFactory.animalNotFound(),"No animal found on invalid fetch");
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("info.dylansouthard.StraysBookAPI.testutils.TestCaseRepo#getAnimalUpdateTestCases")
    @Transactional
    public void When_UpdatingAnimal_AnimalShouldBeUpdatedInAccordanceWithAccessRules(AnimalUpdateTestCase testCase) {

        User user = userRepository.save(DummyTestData.createUser());
        User secondUser = userRepository.save(new User("User B", "abc@abc.com"));

        Animal animal = createAnimalWithPrimaryCaretaker(user, secondUser);

        UpdateAnimalDTO updateDTO = new UpdateAnimalDTO();
        testCase.getUpdates().forEach(updateDTO::addUpdate);

        testCase.getOriginalValues().forEach((key, value) -> {
            AnimalUpdater.applyUpdate(animal, key, value);
        });

        animalRepository.save(animal);

        AnimalDTO updatedAnimal = animalService.updateAnimal(animal.getId(), updateDTO, testCase.isUserIsPrimaryCaretaker() ? user : secondUser);

        Object expectedValue = testCase.isShouldSucceed()
                ? testCase.getUpdates().values().iterator().next()
                :testCase.getOriginalValues().values().iterator().next();

        BeanWrapper udWrapper  = new BeanWrapperImpl(updatedAnimal);
        Object actualValue = udWrapper.getPropertyValue(testCase.getUpdates().keySet().iterator().next());

        assertEquals(
                String.valueOf(expectedValue),
                String.valueOf(actualValue),
                testCase.getDesc() + " Value should match expected update rule"
        );
//        assertEquals(expectedValue, actualValue, testCase.getDesc() + " Value should match expected update rule");
    }


    @Test
    void updateAnimal_shouldThrow_whenAnimalNotFound() {
        Long nonexistentId = -1L;
        UpdateAnimalDTO dto = new UpdateAnimalDTO();
        dto.setUpdates(Map.of("name", "Ghost"));

        User dummyUser = new User();
        dummyUser.setId(1L);

        ExceptionAssertionRunner.assertThrowsExceptionOfType(() -> {
            animalService.updateAnimal(nonexistentId, dto, dummyUser);
        }, ErrorFactory.animalNotFound(), "No animal found on update");
    }

    @Test
    void updateAnimal_shouldThrow_whenUpdateDTOIsNull() {
        User dummyUser = new User();
        dummyUser.setId(1L);

        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> animalService.updateAnimal(1L, null, dummyUser),
                ErrorFactory.invalidParams(),
                "Update animal null dto"
        );

    }

    @Test
    void When_DeletingAnimal_AnimalShouldBeDeletedOnlyByPrimaryCaretaker() {
        User user = userRepository.save(DummyTestData.createUser());
        User secondUser = userRepository.save(new User("User B", "abc@abc.com"));
        Animal animal = createAnimalWithPrimaryCaretaker(user, secondUser);
        ExceptionAssertionRunner.assertThrowsExceptionOfType(()-> animalService.deleteAnimal(animal.getId(), secondUser),ErrorFactory.authForbidden(), "Non-primary caretaker not authorized to delete");

        assertNotNull(animalRepository.findByActiveId(animal.getId()).orElse(null), "Animal should still exist");

        animalService.deleteAnimal(animal.getId(), user);

        assertNull(animalRepository.findByActiveId(animal.getId()).orElse(null), "Deleted animal should not exist");
    }

    @Test
    void When_DeletingNonexistentAnimal_ShouldThrowNotFoundError() {
        long invalidId = 9999L;
        User user = userRepository.save(DummyTestData.createUser());
        ExceptionAssertionRunner.assertThrowsExceptionOfType(()->animalService.deleteAnimal(invalidId, user), ErrorFactory.animalNotFound(),"No animal found on invalid delete");
    }
}
