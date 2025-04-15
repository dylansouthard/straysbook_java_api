package info.dylansouthard.StraysBookAPI.controller;


import info.dylansouthard.StraysBookAPI.BaseDBTest;
import info.dylansouthard.StraysBookAPI.config.DummyTestData;
import info.dylansouthard.StraysBookAPI.dto.user.AuthTokenDTO;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.AuthTokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


    @Test
    public void When_StatusCalledWithValidToken_Expect_AuthenticatedTrue() throws Exception {
        User user = userRepository.save(DummyTestData.createUser());

        // Generate token and simulate login
        AuthTokenDTO tokenDTO = authTokenService.generateAccessToken(user, "test-device");
        String bearerToken = "Bearer " + tokenDTO.getToken();

        mockMvc.perform(get("/api/users/auth/status")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.id").value(user.getId()));
    }

    @Test
    public void When_StatusCalledWithoutToken_Expect_AuthenticatedFalse() throws Exception {
        mockMvc.perform(get("/api/users/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.user").doesNotExist());
    }
}
