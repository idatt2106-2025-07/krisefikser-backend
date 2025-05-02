package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.ChangeEmailRequest;
import com.group7.krisefikser.dto.request.ChangePasswordRequest;
import com.group7.krisefikser.dto.response.GenericMessageResponse;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authenticated user operations such as changing email and password.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Change user password", description = "Allows the currently authenticated user to change their password.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericMessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid password or request data",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized request",
            content = @Content)
    })
    @PostMapping("/change-password")
    public ResponseEntity<GenericMessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GenericMessageResponse("Not logged in"));
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new GenericMessageResponse("Password updated successfully"));
    }

    @Operation(summary = "Change user email", description = "Allows the currently authenticated user to change their email address.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email changed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericMessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid email or request data",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized request",
            content = @Content)
    })
    @PostMapping("/change-email")
    public ResponseEntity<GenericMessageResponse> changeEmail(
            Authentication authentication,
            @Valid @RequestBody ChangeEmailRequest request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GenericMessageResponse("Not logged in"));
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.getNewEmail());
        userRepository.save(user);

        return ResponseEntity.ok(new GenericMessageResponse("Email updated successfully"));
    }
}
