package info.dylansouthard.StraysBookAPI.controller;

import info.dylansouthard.StraysBookAPI.dto.user.AuthStatusDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateUserDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.mapper.UserMapper;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;

    @Autowired
    private final UserService userService;

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

    @PatchMapping("/me")
    @Operation(summary = "Update the current user's profile ", description = "Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH"),
            @ApiResponse(responseCode = "404", description = "Animal not found. Error Code: USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<UserPrivateDTO> updateAnimal(
            @RequestBody UpdateUserDTO updateDTO,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();
        UserPrivateDTO updated = userService.updateUser(user.getId(), updateDTO);
        return ResponseEntity.ok(updated);
    }
}
