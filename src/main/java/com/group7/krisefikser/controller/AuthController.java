package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private static final Logger logger = Logger.getLogger(AuthController.class.getName());

  /**
   * Endpoint for user registration.
   * This method handles the registration of a new user.
   * It accepts a RegisterRequest object containing user details.
   *
   * @param request the registration request containing user details
   * @param response the HTTP response object
   * @return a ResponseEntity containing the authentication response
   */
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(
            @RequestBody RegisterRequest request, HttpServletResponse response) {
    logger.info("Received register request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.registerUser(request, response);

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
   * @param request the login request containing user credentials
   * @param response the HTTP response object
   * @return a ResponseEntity containing the authentication response
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request,
                                                HttpServletResponse response) {
    logger.info("Received login request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.loginUser(request, response);

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
  @GetMapping("/verify-email")
  public ResponseEntity<AuthResponse> verifyEmail(@RequestParam String token) {
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
}
