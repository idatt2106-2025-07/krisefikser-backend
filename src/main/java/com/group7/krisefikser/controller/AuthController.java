package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.request.ResetPasswordLinkRequest;
import com.group7.krisefikser.dto.request.ResetPasswordRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.dto.response.CurrentUserResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.service.UserService;
import com.group7.krisefikser.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling authentication requests.
 * This class provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtUtils jwtUtils;
  private static final Logger logger = Logger.getLogger(AuthController.class.getName());

  /**
   * Endpoint for user registration.
   * This method handles the registration of a new user.
   * It accepts a RegisterRequest object containing user details.
   *
   * @param request the registration request containing user details
   * @return a ResponseEntity containing the authentication response
   */
  @Operation(
      summary = "Register a new user",
      description = "Registers a new user account using the provided email, name and password. "
      + "A verification email is sent to the provided email address."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "User registered successfully",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error while saving the user",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class)))
  })
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(
      @Valid @RequestBody RegisterRequest request) {
    logger.info("Received register request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.registerUser(request);

      logger.info("User registered successfully: " + request.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

    } catch (Exception e) {
      logger.warning("Error registering user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        new AuthResponse(AuthResponseMessage.SAVING_USER_ERROR.getMessage()
          + e.getMessage(), null, null));
    }
  }

  /**
   * Endpoint for user login.
   * This method handles the login of an existing user.
   * It accepts a LoginRequest object containing user credentials.
   *
   * @param request  the login request containing user credentials
   * @param response the HTTP response object
   * @return a ResponseEntity containing the authentication response
   */
  @Operation(
      summary = "Log in a user",
      description = "Logs in a user using their email and password. "
      + "If the credentials are valid and the user is verified, a JWT token is returned."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User logged in successfully",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error during login process",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class)))
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginUser(
      @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
    logger.info("Received login request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.loginUser(request, response);

      if (!authResponse.getMessage().equals(AuthResponseMessage
          .USER_LOGGED_IN_SUCCESSFULLY.getMessage())
          && !authResponse.getMessage().equals(AuthResponseMessage.TWO_FACTOR_SENT.getMessage())) {
        logger.warning("Invalid credentials for user: " + request.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
      }

      logger.info("User logged in successfully: " + request.getEmail());
      return ResponseEntity.ok(authResponse);

    } catch (Exception e) {
      logger.warning("Error logging in user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        new AuthResponse(AuthResponseMessage.USER_LOGIN_ERROR.getMessage()
          + e.getMessage(), null, null));
    }
  }

  /**
   * Endpoint for email verification.
   * This method handles the verification of a user's email.
   * It accepts a token as a request parameter.
   *
   * @param token the verification token
   * @return a ResponseEntity containing the authentication response
   */
  @Operation(
      summary = "Verify user email",
      description = "Verifies a user's email address using a token sent "
      + "to them via email after registration.",
      parameters = @Parameter(
      name = "token",
      description = "Verification token received by email. "
        + "Must match a token issued during registration.",
      required = true,
      schema = @Schema(type = "string")
      )
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User verified successfully",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid or expired verification token",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error during email verification",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AuthResponse.class)))
  })
  @GetMapping("/verify-email")
  public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestParam String token) {
    logger.info("Received email verification request with token: " + token);
    try {
      AuthResponse authResponse = userService.verifyEmail(token);

      if (authResponse.getMessage().equals(
          AuthResponseMessage.USER_VERIFIED_SUCCESSFULLY.getMessage())) {
        logger.info("User verified successfully with token: " + token);
        return ResponseEntity.ok(authResponse);
      } else {
        logger.warning("Error verifying user: " + authResponse.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
      }
    } catch (Exception e) {
      logger.warning("Error verifying email: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        new AuthResponse(AuthResponseMessage.EMAIL_VERIFICATION_ERROR.getMessage()
          + e.getMessage(), null, null));
    }
  }

  /**
   * Endpoint to get the current user's information.
   * This method retrieves the email and name of the currently authenticated user.
   *
   * @return a ResponseEntity containing the current user's information
   */
  @Operation(
      summary = "Get current user info",
      description = "Returns the email and name of the currently"
         + " authenticated user, or no content if not authenticated."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Authenticated – returns email and name",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CurrentUserResponse.class))),
    @ApiResponse(responseCode = "204", description = "Not authenticated – no content")
  })
  @GetMapping("/me")
  public ResponseEntity<CurrentUserResponse> getCurrentUserInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken)) {
      try {
        com.group7.krisefikser.model.User user = userService.getCurrentUser();
        if (user != null) {
          return ResponseEntity.ok(new CurrentUserResponse(user.getEmail(), user.getName()));
        }
      } catch (NumberFormatException e) {
        logger.warning("Error parsing user ID: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new CurrentUserResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null));
      } catch (Exception e) {
        logger.warning("Error retrieving current user: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new CurrentUserResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null));
      }
    }
    
    logger.info("GET /me - No authenticated user found");
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for logging out a user.
   * This method clears the JWT cookie to log out the current user.
   *
   * @param response the HTTP response object
   * @return a ResponseEntity indicating the logout status
   * @throws NoSuchAlgorithmException if an error occurs while setting the JWT cookie
   * @see JwtUtils#setLogOutJwtCookie(HttpServletResponse)
   */
  @Operation(
      summary = "Log out a user",
      description = "Clears the JWT cookie to log out the current user."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User logged out successfully")
  })
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    try {
      jwtUtils.setLogOutJwtCookie(response);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      logger.warning("Error during logout: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint for resetting the password.
   * This method handles the password reset process.
   * It accepts a ResetPasswordRequest object containing the email and new password.
   *
   * @param resetPasswordRequest the request containing the email
   *                             and new password
   */
  @Operation(
      summary = "Reset password",
      description = "Resets a user's password using a valid token and new password. "
          + "Typically used after clicking a reset link sent to email."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server error during password reset",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponse.class)))
  })
  @PostMapping("/reset-password")
  public ResponseEntity<AuthResponse> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    logger.info("Received password reset request for user: " + resetPasswordRequest.getEmail());
    try {
      AuthResponse authResponse = userService.resetPassword(resetPasswordRequest);
      if (authResponse.getMessage().equals(
          AuthResponseMessage.PASSWORD_RESET_SUCCESS.getMessage())) {
        logger.info("Password reset successfully for user: " + resetPasswordRequest.getEmail());
        return ResponseEntity.ok(authResponse);
      } else {
        logger.warning("Error resetting password: " + authResponse.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
      }
    } catch (Exception e) {
      logger.warning("Error resetting password: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new AuthResponse(AuthResponseMessage.PASSWORD_RESET_ERROR.getMessage()
                  + e.getMessage(), null, null));
    }
  }

  /**
   * Endpoint for sending a new password link.
   * This method sends a new password link to the user's email.
   * It accepts a ResetPasswordRequest object containing the email.
   * The link contains a token used to reset the password.
   *
   * @param request the request containing the email
   * @return a ResponseEntity indicating the result of the operation
   */
  @Operation(
      summary = "Send reset password link",
      description = "Sends a reset password link to the user's email if the user exists. "
          + "The link contains a token used to reset the password."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reset link sent successfully",
          content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Server error when sending reset link",
          content = @Content(mediaType = "application/json"))
  })
  @PostMapping("/new-password-link")
  public ResponseEntity<Object> sendNewPasswordLink(
      @Valid @RequestBody ResetPasswordLinkRequest request) {
    
    String email = request.getEmail();
    logger.info("Trying to send new password link to: " + email);
    try {
      userService.sendResetPasswordLink(email);
      logger.info("New password link sent to: " + email);
      return ResponseEntity.ok("New password link sent to: " + email);
    } catch (Exception e) {
      logger.severe("Error sending new password link to " + email + ": " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}