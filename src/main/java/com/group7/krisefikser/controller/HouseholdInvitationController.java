package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.InvitationRequest;
import com.group7.krisefikser.exception.InvitationNotFoundException;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.service.HouseholdInvitationService;
import com.group7.krisefikser.service.UserService;
import com.group7.krisefikser.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing household invitations.
 * Provides endpoints for creating and accepting invitations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/household-invitations")
@Tag(name = "Household Invitation", description = "API for managing household invitations")
public class HouseholdInvitationController {
  private final HouseholdInvitationService invitationService;
  private static final Logger logger = Logger
      .getLogger(HouseholdInvitationController.class.getName());
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final UserService userService;

  /**
   * Creates a new household invitation.
   *
   * @param request The invitation request containing email.
   * @return A ResponseEntity containing the created HouseholdInvitation object.
   */
  @PostMapping
  @Operation(summary = "Create a household invitation",
      description = "Creates an invitation to join a household and sends an email to the invitee")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invitation created successfully",
      content = @Content(schema = @Schema(implementation = HouseholdInvitation.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "JSON object containing the email of the person to invite",
      required = true,
      content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = InvitationRequest.class)
        )
  )
  public ResponseEntity<?> createInvitation(@RequestBody InvitationRequest request) {
    try {
      if (request.getEmail() == null || request.getEmail().isBlank()) {
        return ResponseEntity.badRequest().body("Email must not be empty");
      }

      String emailRegex =
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
      if (!request.getEmail().matches(emailRegex)) {
        return ResponseEntity.badRequest().body("Invalid email format");
      }

      String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
      Long userId = Long.parseLong(userIdStr);

      HouseholdInvitation invitation = invitationService.createInvitation(userId,
          request.getEmail());
      logger.info("Invitation created successfully with token: " + invitation.getInvitationToken());
      return ResponseEntity.ok(invitation);

    } catch (IllegalArgumentException e) {
      logger.severe("Invalid request data: " + e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      logger.severe("Unexpected error while creating invitation" + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
    }
  }

  /**
   * Verifies a household invitation using a token.
   *
   * @param token The invitation token provided as a query parameter.
   * @return A ResponseEntity containing the invitation details if valid.
   */
  @GetMapping("/verify")
  @Operation(
      summary = "Verify a household invitation",
      description = "Verifies if an invitation token is valid and returns invitation details",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
          name = "token",
          description = "The invitation token to verify",
          required = true,
          schema = @Schema(type = "string"),
          in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
          )
      }
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invitation is valid"),
    @ApiResponse(responseCode = "404", description = "Invitation not found or expired")
  })
  public ResponseEntity<?> verifyInvitation(@RequestParam String token) {
    logger.info("Verify invitation token: " + token);
    HouseholdInvitation invitation = invitationService.verifyInvitation(token);
    return ResponseEntity.ok(invitation);
  }

  /**
   * Accepts a household invitation using a token.
   *
   * @param requestBody The request body containing the invitation token.
   * @return A ResponseEntity indicating the result of the acceptance.
   */
  @PostMapping("/accept")
  @Operation(
      summary = "Accept a household invitation",
      description = "Accepts an invitation using the provided token"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
    @ApiResponse(responseCode = "400", description = "Missing or invalid token"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Invitation not found or expired"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "JSON object containing the invitation token",
      required = true,
      content = @Content(
      mediaType = "application/json",
      schema = @Schema(
        type = "object",
        example = "{\"token\": \"invitation-token-value\"}",
        requiredProperties = {"token"}
      )
      )
  )
  public ResponseEntity<?> acceptInvitation(@RequestBody Map<String, String> requestBody) {
    try {
      String token = requestBody.get("token");

      if (token == null || token.isBlank()) {
        return ResponseEntity.badRequest().body("Token is required");
      }

      String email = jwtUtils.validateInvitationTokenAndGetEmail(token);
      Long userId = userService.getUserIdByEmail(email);

      invitationService.acceptInvitation(token, userId);
      logger.info("Invitation accepted successfully");
      return ResponseEntity.ok().build();

    } catch (NumberFormatException e) {
      logger.severe("Invalid user ID in security context" + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication context");

    } catch (IllegalArgumentException e) {
      logger.severe("Invalid token or user: " + e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (InvitationNotFoundException e) {
      logger.severe("Invitation not found or expired: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invitation not found or expired");

    } catch (JwtMissingPropertyException e) {
      logger.severe("Missing expected property in token: " + e.getMessage());
      return ResponseEntity.badRequest().body("Invalid token format");

    } catch (Exception e) {
      logger.severe("Unexpected error while accepting invitation" + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
    }
  }
}
