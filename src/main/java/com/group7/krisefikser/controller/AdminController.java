package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.dto.request.TwoFactorLoginRequest;
import com.group7.krisefikser.service.AdminService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for handling requests related to admins.
 * This class provides endpoints to register and invite admins.
 */
@Controller
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  private static final Logger logger = Logger.getLogger(AdminController.class.getName());

  /**
   * Endpoint to register an admin.
   * This endpoint will accept a request containing the invite token and other registration details.
   *
   * @param request The request containing the invite token and other registration details.
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Register an admin",
      description = "Registers a new admin using the provided invite token, email, and password. "
          + "The token must be valid and generated through the admin invitation process.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "RegisterAdminRequest with token, email, and secure password",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RegisterAdminRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Admin registered successfully",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Validation error in request body (e.g., invalid email, weak password)",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody @Valid RegisterAdminRequest request,
                                         BindingResult bindingResult) {
    logger.info("Registering admin request");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      adminService.registerAdmin(request);
      logger.info("Admin registered successfully");
      return ResponseEntity.ok("Admin registered successfully");
    } catch (Exception e) {
      logger.severe("Error registering admin: " + e.getMessage());
      return ResponseEntity.status(500).body("Error registering admin");
    }
  }

  /**
   * Endpoint to handle two-factor authentication for admin login.
   * This endpoint will accept a request containing the token for two-factor authentication.
   *
   * @param request The request containing the token for two-factor authentication.
   * @param response The HTTP response object.
   * @return ResponseEntity indicating the result of the operation.
   */
  @Operation(
      summary = "Verify two-factor authentication token",
      description = "Verifies the provided two-factor authentication token for admin login. "
          + "If the token is valid, a JWT is issued and set in a cookie.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "TwoFactorLoginRequest containing the 2FA token",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TwoFactorLoginRequest.class)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Two-factor authentication verified successfully",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Validation error: Token is missing or invalid format",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error during verification",
              content = @Content(mediaType = "application/json")
          )
      }
  )
  @PostMapping("/2fa")
  public ResponseEntity<Object> twoFactorAuthentication(
      @RequestBody @Valid TwoFactorLoginRequest request,
      HttpServletResponse response, BindingResult bindingResult) {
    logger.info("Two Factor Authentication request");

    if (bindingResult.hasErrors()) {
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      adminService.verifyTwoFactor(request.getToken(), response);
      logger.info("Two Factor Authentication verified successfully");
      return ResponseEntity.ok("Two Factor Authentication verified successfully");
    } catch (Exception e) {
      logger.severe("Error verifying Two Factor Authentication: " + e.getMessage());
      return ResponseEntity.status(500).body("Error verifying Two Factor Authentication");
    }
  }
}
