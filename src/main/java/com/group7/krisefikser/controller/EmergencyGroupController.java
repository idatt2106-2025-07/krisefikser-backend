package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.request.InvitationReplyRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupInvitationResponse;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.dto.response.ErrorResponse;
import com.group7.krisefikser.service.EmergencyGroupService;
import com.group7.krisefikser.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling HTTP requests related to emergency groups.
 * This class contains endpoints for retrieving and adding emergency groups.
 * It uses the EmergencyGroupService to perform the operations.
 */
@RestController
@RequestMapping("/api/emergency-groups")
@RequiredArgsConstructor
@Tag(name = "Emergency Group", description = "Endpoints for managing emergency groups")
public class EmergencyGroupController {
  private final EmergencyGroupService emergencyGroupService;
  private final Logger logger = LoggerFactory.getLogger(EmergencyGroupController.class);

  /**
   * Retrieves the EmergencyGroup object with the specified ID from the repository.
   *
   * @param id the ID of the EmergencyGroup to retrieve
   * @return the EmergencyGroupResponse object with the specified ID
   */
  @Operation(
          summary = "Get Emergency Group by ID",
          description = "Retrieve an emergency group by its ID.",
          parameters = {
            @Parameter(name = "id", description = "ID of the emergency group to retrieve")
          },
          responses = {
            @ApiResponse(responseCode = "200",
                    description = "Emergency group retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmergencyGroupResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Emergency group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @GetMapping("/{id}")
  public ResponseEntity<Object> getEmergencyGroupById(@PathVariable Long id) {
    try {
      EmergencyGroupResponse response = emergencyGroupService.getEmergencyGroupById(id);
      logger.info("Emergency group with ID {} retrieved successfully.", id);
      return ResponseEntity.ok(response);
    } catch (NoSuchElementException e) {
      logger.error("Emergency group with ID {} not found.", id);
      return ResponseEntity.status(404).body(new ErrorResponse("Emergency group not found. "
              + "The emergency group with the specified ID does not exist."
      ));
    } catch (Exception e) {
      logger.error("An error occurred while retrieving emergency group with ID {}: {}",
              id, e.getMessage());
      return ResponseEntity.status(500).body("An error occurred while retrieving the "
              + "emergency group.");
    }
  }

  /**
   * Adds a new EmergencyGroup to the repository.
   *
   * @param request the EmergencyGroup object to add
   * @return the EmergencyGroupResponse object representing the added group
   */
  @Operation(
          summary = "Add Emergency Group",
          description = "Add a new emergency group.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Emergency group request object",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = EmergencyGroupRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "201",
                    description = "Emergency group added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmergencyGroupResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, invalid data in the request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PostMapping
  public ResponseEntity<Object> addEmergencyGroup(@RequestBody EmergencyGroupRequest request) {
    try {
      EmergencyGroupResponse response = emergencyGroupService.addEmergencyGroup(request);
      logger.info("Emergency group {} added successfully.", response.getName());
      return ResponseEntity.status(201).body(response);
    } catch (IllegalArgumentException e) {
      logger.error("Failed to add emergency group: {}", e.getMessage());
      return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.error("An error occurred while adding the emergency group: {}", e.getMessage());
      return ResponseEntity.status(500).body("An error occurred while adding the emergency group.");
    }
  }

  /**
   * Invites a household to an emergency group by its name.
   *
   * @param householdName the name of the household to invite.
   * @return a response entity indicating the result of the operation
   */
  @Operation(
          summary = "Invite Household to Emergency Group",
          description = "Invite a household to an emergency group by its name.",
          parameters = {
            @Parameter(name = "householdName", description = "Name of the household to invite")
          },
          responses = {
            @ApiResponse(responseCode = "200",
                    description = "Household invited successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Household not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PostMapping("/invite/{householdName}")
  public ResponseEntity<Object> inviteHouseholdByName(@PathVariable String householdName) {
    logger.info("Inviting household with name: {}", householdName);
    try {
      emergencyGroupService.inviteHouseholdByName(householdName);
      logger.info("Household {} invited successfully.", householdName);
      return ResponseEntity.ok("Household invited successfully.");
    } catch (NoSuchElementException e) {
      logger.error("Household with name {} not found.", householdName);
      return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    } catch (IllegalArgumentException e) {
      logger.error("Failed to invite household: {}", e.getMessage());
      return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.error("An unexpected error occurred while inviting the household: {}", e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "An unexpected error occurred while inviting the household."
      ));
    }
  }

  /**
   * Answers the invitation to an emergency group.
   * This method allows a user to accept or decline an invitation
   * to join an emergency group.
   *
   * @param groupId       the ID of the group to answer the invitation for
   * @param request       the request object containing the user's response
   * @param bindingResult the binding result for validation errors
   * @return a response entity indicating the result of the operation
   */
  @Operation(
          summary = "Answer Emergency Group Invitation",
          description = "Answer an invitation to join an emergency group.",
          parameters = {
            @Parameter(name = "groupId", description =
                    "ID of the emergency group to answer the invitation for")
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Invitation reply request object",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = InvitationReplyRequest.class))
          ),
          responses = {
            @ApiResponse(responseCode = "200",
                    description = "Invitation answered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404",
                    description = "Emergency group not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request, invalid response",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @PatchMapping("/answer-invitation/{groupId}")
  public ResponseEntity<Object> answerInvitation(
          @PathVariable Long groupId,
          @Valid @RequestBody InvitationReplyRequest request,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      logger.error("Validation failed: {}", bindingResult.getAllErrors());
      return ValidationUtils.handleValidationErrors(bindingResult);
    }
    try {
      emergencyGroupService.answerEmergencyGroupInvitation(groupId, request.getIsAccept());
      logger.info("Invitation answered successfully.");
      return ResponseEntity.ok("Invitation answered successfully.");
    } catch (NoSuchElementException e) {
      logger.error("Emergency group with ID {} not found.", groupId);
      return ResponseEntity.status(404).body(new ErrorResponse("Emergency group not found."));
    } catch (IllegalArgumentException e) {
      logger.error("Failed to answer invitation: {}", e.getMessage());
      return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
      logger.error("An unexpected error occurred while answering the invitation: {}",
              e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "An unexpected error occurred while answering the invitation."
      ));
    }
  }

  /**
   * Retrieves the list of invitations for the current user's household.
   *
   * @return a response entity containing the list of invitations
   */
  @Operation(
          summary = "Get Invitations",
          description = "Retrieve the list of invitations for the current user's household.",
          responses = {
            @ApiResponse(responseCode = "200",
                    description = "Invitations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =
                                    EmergencyGroupInvitationResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
          }
  )
  @GetMapping("/invitations")
  public ResponseEntity<Object> getInvitations() {
    logger.info("Retrieving group invitations for the current users household.");

    try {
      List<EmergencyGroupInvitationResponse> invitations =
              emergencyGroupService.getEmergencyGroupInvitationsForCurrentUser();
      logger.info("Retrieved invitations successfully.");
      return ResponseEntity.ok(invitations);
    } catch (Exception e) {
      logger.error("An unexpected error occurred while retrieving invitations: {}",
              e.getMessage());
      return ResponseEntity.status(500).body(new ErrorResponse(
              "An unexpected error occurred while retrieving invitations."
      ));
    }
  }
}
