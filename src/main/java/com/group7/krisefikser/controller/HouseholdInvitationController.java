package com.group7.krisefikser.controller;

import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.model.InvitationRequest;
import com.group7.krisefikser.service.HouseholdInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
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
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> createInvitation(@RequestBody InvitationRequest request) {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);

    HouseholdInvitation invitation = invitationService.createInvitation(userId, request.getEmail());
    logger.info("Invitation created successfully with token: " + invitation.getInvitationToken());
    return ResponseEntity.ok(invitation);
  }

  /**
   * Verifies a household invitation using a token.
   *
   * @param token The invitation token provided as a query parameter.
   * @return A ResponseEntity containing the invitation details if valid.
   */
  @GetMapping("/verify")
  @Operation(summary = "Verify a household invitation",
      description = "Verifies if an invitation token is valid and returns invitation details")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invitation is valid"),
    @ApiResponse(responseCode = "404", description = "Invitation not found or expired")
  })
  public ResponseEntity<?> verifyInvitation(@RequestParam String token) {
    HouseholdInvitation invitation = invitationService.verifyInvitation(token);
    return ResponseEntity.ok(invitation);
  }

  /**
   * Accepts a household invitation using a token.
   *
   * @param requestBody The request body containing the invitation token.
   * @return A ResponseEntity indicating the result of the acceptance.
   * @throws JwtMissingPropertyException if the token is missing required properties.
   */
  @PostMapping("/accept")
  @Operation(summary = "Accept a household invitation",
      description = "Accepts an invitation using the provided token")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
    @ApiResponse(responseCode = "400", description = "Missing or invalid token"),
    @ApiResponse(responseCode = "404", description = "Invitation not found or expired")
  })
  public ResponseEntity<?> acceptInvitation(@RequestBody Map<String, String> requestBody)
      throws JwtMissingPropertyException {
    String token = requestBody.get("token");

    if (token == null || token.isEmpty()) {
      return ResponseEntity.badRequest().body("Token is required");
    }

    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);

    invitationService.acceptInvitation(token, userId);
    logger.info("Invitation accepted successfully");
    return ResponseEntity.ok().build();
  }
}
