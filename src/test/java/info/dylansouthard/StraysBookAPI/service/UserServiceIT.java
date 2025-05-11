/**
 * Integration tests for user-related services.
 *
 * This file contains integration tests for the UserService and related user functionality.
 * Tests are organized by function group, including:
 *   - User Update Tests
 *   - User Deletion Tests
 *   - Watchlist Tests
 */
package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.cases.UserUpdateTestCase;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateUserDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.friendo.Animal;
import info.dylansouthard.StraysBookAPI.model.user.AuthToken;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.AuthTokenRepository;
import info.dylansouthard.StraysBookAPI.testutils.ExceptionAssertionRunner;
import info.dylansouthard.StraysBookAPI.util.updaters.UserUpdater;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserServiceIT extends BaseDBTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    // ============================================================
    // User Update Tests
    // ============================================================

    /**
     * Tests updating user fields with various update rules and ensures the user is updated according to expected rules.
     * Uses parameterized test cases to verify both valid and invalid update scenarios.
     */
    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("info.dylansouthard.StraysBookAPI.testutils.TestCaseRepo#getUserUpdateTestCases")
    @Transactional
    public void When_UpdatingUser_Expect_UserUpdatedAccordingToRules(UserUpdateTestCase testCase) {
        User user = userRepository.save(DummyTestData.createUser());

        UpdateUserDTO updateDTO = new UpdateUserDTO();
        testCase.getUpdates().forEach(updateDTO::addUpdate);

        testCase.getOriginalValues().forEach((key, value) -> {
            UserUpdater.applyUpdate(user, key, value);
        });

        User savedUser = userRepository.save(user);

        if (!testCase.isShouldSucceed() && testCase.getUpdates().size() < 2) {
            ExceptionAssertionRunner.assertThrowsExceptionOfType(
                    () -> userService.updateUser(savedUser.getId(), updateDTO),
                    ErrorFactory.invalidParams(),
                    "Invalid User Updates"
            );
            return;
        }

        UserPrivateDTO updatedUserDTO = userService.updateUser(savedUser.getId(), updateDTO);

        BeanWrapper udWrapper = new BeanWrapperImpl(updatedUserDTO);
        Object actualValue = udWrapper.getPropertyValue(testCase.getUpdates().keySet().iterator().next());

        Object expectedValue = testCase.isShouldSucceed()
                ? testCase.getUpdates().values().iterator().next()
                : testCase.getOriginalValues().values().iterator().next();

        assertEquals(
                String.valueOf(expectedValue),
                String.valueOf(actualValue),
                testCase.getDesc() + " Value should match expected update rule"
        );
    }

    // ============================================================
    // User Deletion Tests
    // ============================================================

    /**
     * Tests that deleting a user removes the user and associated auth tokens from the database.
     */
    @Transactional
    @Test
    public void When_DeletingUser_Expect_UserAndAuthTokensDeleted() {
        User user = userRepository.save(DummyTestData.createUser());
        authTokenService.generateRefreshToken(user, "device123");
        List<AuthToken> presentTokens = authTokenRepository.findByUserId(user.getId());
        userService.deleteUser(user.getId());
        List<AuthToken> tokens = authTokenRepository.findByUserId(user.getId());
        assertAll(
                "user deletion assertions",
                () -> assertEquals(1, presentTokens.size(), "Auth tokens for should be found before deletion"),
                () -> assertTrue(userRepository.findActiveById(user.getId()).isEmpty(), "deleted user should have been deleted"),
                () -> assertEquals(0, tokens.size(), "Auth tokens for deleted user should not be found")
        );
    }

    /**
     * Tests that deleting a nonexistent user throws the appropriate user not found error.
     */
    @Test
    public void When_DeletingNonexistentUser_Expect_ThrowsError() {
        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> userService.deleteUser(123L),
                ErrorFactory.userNotFound(),
                "Delete nonexistent user"
        );
    }

    // ============================================================
    // Watchlist Tests
    // ============================================================

    /**
     * Tests adding and removing an animal to/from a user's watchlist and verifies the animal is correctly added/removed.
     */
    @Test
    public void When_AddingToggledInWatchlist_Expect_AnimalToggled() {
        User user = userRepository.save(DummyTestData.createUser());
        Animal animal = animalRepository.save(DummyTestData.createAnimal());
        UserPrivateDTO addDTO = userService.updateWatchlist(user.getId(), animal.getId());

        assertAll("add animal to watchlist assertions",
                () -> assertEquals(1, addDTO.getWatchedAnimals().size(), "Should have one watched animal"),
                () -> assertEquals(animal.getId(), addDTO.getWatchedAnimals().iterator().next().getId())
        );

        UserPrivateDTO removeDTO = userService.updateWatchlist(user.getId(), animal.getId());

        assertEquals(0, removeDTO.getWatchedAnimals().size(), "Should have no animals in watchlist");
    }

    /**
     * Tests that adding an invalid (nonexistent) animal to a user's watchlist throws the appropriate error.
     */
    @Test
    public void When_AddingInvalidAnimalToWatchlist_Expect_ThrowsError() {
        User user = userRepository.save(DummyTestData.createUser());

        Animal animal = DummyTestData.createAnimal();
        animal.setId(234L);
        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> userService.updateWatchlist(user.getId(), animal.getId()),
                ErrorFactory.animalNotFound(),
                "Add invalid animal to watchlist"
        );
    }

    /**
     * Tests that adding an animal to the watchlist of an invalid (nonexistent) user throws the appropriate error.
     */
    @Test
    public void When_AddingAnimalToWatchlistOfInvalidUser_Expect_ThrowsError() {
        User user = DummyTestData.createUser();
        user.setId(234L);
        Animal animal = animalRepository.save(DummyTestData.createAnimal());
        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                () -> userService.updateWatchlist(user.getId(), animal.getId()),
                ErrorFactory.userNotFound(),
                "Add invalid animal to watchlist"
        );
    }
}
