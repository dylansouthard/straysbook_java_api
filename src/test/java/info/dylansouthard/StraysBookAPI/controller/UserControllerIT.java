package info.dylansouthard.StraysBookAPI.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.cases.UserUpdateTestCase;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.constants.ApiRoutes;
import info.dylansouthard.StraysBookAPI.dto.user.AuthTokenDTO;
import info.dylansouthard.StraysBookAPI.errors.AppException;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.AuthTokenService;
import info.dylansouthard.StraysBookAPI.testutils.ExceptionAssertionRunner;
import info.dylansouthard.StraysBookAPI.testutils.TestSecurityUtil;
import info.dylansouthard.StraysBookAPI.util.updaters.UserUpdater;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static info.dylansouthard.StraysBookAPI.config.DummyTestData.createUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserControllerIT extends BaseDBTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void When_FetchingUserByIdWithValidId_ExpectUserReturned() throws Exception {
        User user = userRepository.save(DummyTestData.createUser());

        mockMvc.perform(get(ApiRoutes.USERS.BASE + "/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.displayName").value(user.getDisplayName()));
    }

    @Test
    @Transactional
    public void When_FetchingUserByIdWithInvalidId_ExpectNotFoundError() throws Exception {
        long invalidId = 999999L; // Assuming it doesn't exist

        ResultActions result = mockMvc.perform(get(ApiRoutes.USERS.BASE + "/" + invalidId))
                .andExpect(status().isNotFound());

        ExceptionAssertionRunner.assertThrowsExceptionOfType(result, ErrorFactory.userNotFound());
    }

    @Test
    @Transactional
    public void When_FetchingOwnProfileAuthenticated_ExpectProfileReturned() throws Exception {
        User user = userRepository.save(DummyTestData.createUser());
        String bearerToken = "Bearer " + authTokenService.generateAccessToken(user, "test-device").getToken();

        mockMvc.perform(get(ApiRoutes.USERS.ME)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @Transactional
    public void When_FetchingOwnProfileUnauthenticated_ExpectUnauthorizedError() throws Exception {
        String invalidToken = "Bearer " + "1234567890";

        ResultActions result = mockMvc.perform(get(ApiRoutes.USERS.ME)
                        .header(HttpHeaders.AUTHORIZATION, invalidToken))
                .andExpect(status().isUnauthorized());

        ExceptionAssertionRunner.assertThrowsExceptionOfType(result, ErrorFactory.auth());
    }



    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("info.dylansouthard.StraysBookAPI.testutils.TestCaseRepo#getUserUpdateTestCases")
    @Transactional
    public void When_UpdateUser_ExpectUserUpdatedAccordingly(UserUpdateTestCase testCase) throws Exception {

        // Create user and authenticate
        User user = userRepository.save(createUser());
        TestSecurityUtil.authenticateTestUser(user);

        // Prepare the update payload from the test case
        Map<String, Object> updateJson = new HashMap<>(testCase.getUpdates());

        // Apply original values to the user so that updates are measurable
        testCase.getOriginalValues().forEach((key, value) -> {
            UserUpdater.applyUpdate(user, key, value);
        });

        // Persist the modified user to the database
        userRepository.save(user);

        // Determine which field is being updated and what the expected result should be
        String field = testCase.getUpdates().keySet().iterator().next();


        // Perform the PATCH request and assert the correct HTTP status
        ResultActions result = mockMvc.perform(patch(ApiRoutes.USERS.ME)
                .with(TestSecurityUtil.testUser(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJson)));


        if (testCase.getThrowsError() == null) {

            Object expectedValue = testCase.isShouldSucceed()
                    ? testCase.getUpdates().values().iterator().next()
                    : testCase.getOriginalValues().values().iterator().next();

            result.andExpect(status().isOk());

            result.andExpect(jsonPath("$." + field).value(String.valueOf(expectedValue)));

        } else {
            AppException exception = ErrorFactory.getErrorOfType(testCase.getThrowsError());
            ExceptionAssertionRunner.assertThrowsExceptionOfType(result, exception);
        }

    }

    @Test
    @Transactional
    public void When_DeletingUser_ExpectUserDeleted() throws Exception {
        User user = userRepository.save(createUser());

        // Generate token and simulate login
        AuthTokenDTO tokenDTO = authTokenService.generateAccessToken(user, "test-device");
        String bearerToken = "Bearer " + tokenDTO.getToken();

        mockMvc.perform(delete(ApiRoutes.USERS.ME)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void When_DeletingUserWhileUnauthorized_ExpectUserThrowsError() throws Exception {

        String bearerToken = "Bearer " + "1234567890";

        ResultActions result = mockMvc.perform(delete(ApiRoutes.USERS.ME)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken));

        ExceptionAssertionRunner.assertThrowsExceptionOfType(result, ErrorFactory.auth());
    }
}
