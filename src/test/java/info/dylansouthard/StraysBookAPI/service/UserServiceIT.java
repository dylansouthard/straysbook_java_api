package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.cases.UserUpdateTestCase;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateUserDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.testutils.ExceptionAssertionRunner;
import info.dylansouthard.StraysBookAPI.util.updaters.UserUpdater;
import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class UserServiceIT extends BaseDBTest {

    @Autowired
    private UserService userService;

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
}
