package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private static final Logger logger = Logger.getLogger(AuthController.class.getName());


  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request, HttpServletResponse response) {
    logger.info("Received register request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.registerUser(request, response);

      logger.info("User registered successfully: " + request.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

    } catch (Exception e) {
      logger.warning("Error registering user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new AuthResponse(request.getEmail(),
              AuthResponseMessage.SAVING_USER_ERROR.getMessage()
                  + e.getMessage(), null, null));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
    logger.info("Received login request for user: " + request.getEmail());
    try {
      AuthResponse authResponse = userService.loginUser(request);

      logger.info("User logged in successfully: " + request.getEmail());
      return ResponseEntity.ok(authResponse);

    } catch (Exception e) {
      logger.warning("Error logging in user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new AuthResponse(request.getEmail(),
              AuthResponseMessage.USER_LOGIN_ERROR.getMessage()
                  + e.getMessage(), null, null));
    }
  }
}
