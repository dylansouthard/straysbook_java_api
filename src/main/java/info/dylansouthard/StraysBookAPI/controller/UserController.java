package info.dylansouthard.StraysBookAPI.controller;

import info.dylansouthard.StraysBookAPI.constants.ApiRoutes;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateUserDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UpdateWatchlistDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPublicDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    //region VARIABLES================================================================================================
    @Autowired
    private final UserService userService;
    //endregion

    //region READ=====================================================================================================
    @GetMapping(ApiRoutes.USERS.DETAIL)
    @Operation(summary = "Fetch detailed user information", description = "Retrieve full user details by ID.")
    @ApiResponse(responseCode = "200", description = "User found and returned")
    @ApiResponse(responseCode = "404", description = "User not found. Error Code: USER_NOT_FOUND")
    @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    public ResponseEntity<UserPublicDTO> getUserById(@Valid @PathVariable("id") Long id) {
        UserPublicDTO user = userService.fetchUserDetails(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping(ApiRoutes.USERS.ME)
    @Operation(summary = "Fetch a user's own profile", description = "Fetches an user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "401", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found. Error Code: USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<UserPrivateDTO> getMyProfile(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();

       UserPrivateDTO dto = userService.fetchMyProfile(user);

        return ResponseEntity.ok(dto);
    }
    //endregion

    //region UPDATE=====================================================================================================

    @PatchMapping(ApiRoutes.USERS.ME)
    @Operation(summary = "Update the current user's profile ", description = "Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH"),
            @ApiResponse(responseCode = "404", description = "User not found. Error Code: USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<UserPrivateDTO> updateMyProfile(
            @RequestBody UpdateUserDTO updateDTO,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();
        UserPrivateDTO updated = userService.updateUser(user.getId(), updateDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping(ApiRoutes.USERS.WATCHLIST)
    @Operation(summary = "Update the current user's profile ", description = "Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "401", description = "Not logged in. Error Code: AUTH"),
            @ApiResponse(responseCode = "403", description = "Unauthorized. Error Code: AUTH_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "User not found. Error Code: USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<UserPrivateDTO> updateWatchlist(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWatchlistDTO updateDTO,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();
        if (!user.getId().equals(id)) throw ErrorFactory.authForbidden();
        if (updateDTO.getAnimalId() == null) throw ErrorFactory.invalidParams();
        UserPrivateDTO updated = userService.updateWatchlist(id, updateDTO.getAnimalId());
        return ResponseEntity.ok(updated);
    }


    //endregion

    //region DELETE=====================================================================================================

    @DeleteMapping(ApiRoutes.USERS.ME)
    @Operation(summary = "Delete a user's profile", description = "Deletes an user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found. Error Code: USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal User user) {
        if (user == null) throw ErrorFactory.auth();
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    //endregion
}
