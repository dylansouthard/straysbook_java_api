package info.dylansouthard.StraysBookAPI.controller;

import info.dylansouthard.StraysBookAPI.dto.user.AuthTokenPairDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.service.AuthTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthTokenController {
    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "403", description = "Forbidden. Error Code: AUTH_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<AuthTokenPairDTO> refreshAccessToken(
            @RequestParam String refreshToken,
            @RequestParam String deviceId
    ) {
        AuthTokenPairDTO newAccessToken = authTokenService.refreshAccessToken(refreshToken, deviceId);
        return ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/revoke")
    @Operation(summary = "Revoke token for a specific device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token revoked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH"),
            @ApiResponse(responseCode = "403", description = "Forbidden. Error Code: AUTH_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Void> revokeDeviceToken(
            @RequestParam String deviceId,
            @AuthenticationPrincipal User user
            ) {
        if (user == null) throw ErrorFactory.auth();
        authTokenService.revokeTokensForDevice(user, deviceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/revoke-all")
    @Operation(summary = "Revoke all tokens for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All tokens revoked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters. Error Code: INVALID_PARAMS"),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Error Code: AUTH"),
            @ApiResponse(responseCode = "500", description = "Server error. Error Code: INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Void> revokeAllTokens(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) throw ErrorFactory.auth();
        authTokenService.revokeAllTokensForUser(user);
        return ResponseEntity.noContent().build();
    }
}
