package info.dylansouthard.StraysBookAPI.controller;

import info.dylansouthard.StraysBookAPI.dto.user.AuthStatusDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.mapper.UserMapper;
import info.dylansouthard.StraysBookAPI.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;

    @GetMapping("/auth/status")
    @Operation(summary = "Check user authentication status", description = "Returns authentication status and user info if authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<AuthStatusDTO> checkUserStatus(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.ok(new AuthStatusDTO(false, null));
        UserPrivateDTO userDTO = userMapper.toUserPrivateDTO(user);
        return ResponseEntity.ok(new AuthStatusDTO(true, userDTO));
    }
}
