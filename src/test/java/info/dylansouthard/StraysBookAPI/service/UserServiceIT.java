package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.cases.UserUpdateTestCase;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateUserDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
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
                ()-> userService.updateUser(savedUser.getId(), updateDTO),
                ErrorFactory.invalidParams(),
                "Invalid User Updates"
        );
        return;
    }

        UserPrivateDTO updatedUserDTO = userService.updateUser(savedUser.getId(), updateDTO);

        BeanWrapper udWrapper  = new BeanWrapperImpl(updatedUserDTO);
        Object actualValue = udWrapper.getPropertyValue(testCase.getUpdates().keySet().iterator().next());

        Object expectedValue = testCase.isShouldSucceed()
                ? testCase.getUpdates().values().iterator().next()
                :testCase.getOriginalValues().values().iterator().next();

        assertEquals(
                String.valueOf(expectedValue),
                String.valueOf(actualValue),
                testCase.getDesc() + " Value should match expected update rule"
        );
    }

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
                ()->assertEquals(1, presentTokens.size(), "Auth tokens for should be found before deletion"),
                ()->assertTrue(userRepository.findActiveById(user.getId()).isEmpty(), "deleted user should have been deleted"),
                ()->assertEquals(0, tokens.size(), "Auth tokens for deleted user should not be found")
                );
    }

    @Test
    public void When_DeletingNonexistentUser_Expect_ThrowsError() {
        ExceptionAssertionRunner.assertThrowsExceptionOfType(
                ()->userService.deleteUser(123L),
                ErrorFactory.userNotFound(),
                "Delete nonexistent user"
        );
    }
}
