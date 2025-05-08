package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.HouseholdJoinRequest;
import com.group7.krisefikser.dto.request.HouseholdRequest;
import com.group7.krisefikser.dto.request.JoinHouseholdRequest;
import com.group7.krisefikser.dto.response.HouseholdDetailsResponse;
import com.group7.krisefikser.dto.response.GetHouseholdMembersResponse;
import com.group7.krisefikser.dto.response.HouseholdResponse;
import com.group7.krisefikser.dto.response.JoinHouseholdRequestResponse;
import com.group7.krisefikser.dto.response.ReadinessResponse;
import com.group7.krisefikser.mapper.HouseholdMapper;
import com.group7.krisefikser.mapper.JoinRequestMapper;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.service.HouseholdService;
import com.group7.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing household-related operations.
 * Provides endpoints for creating households, handling join requests,
 * and managing household memberships.
 */
@RestController
@RequestMapping("/api/households")
@Tag(name = "Household Management", description = "APIs for household operations and membership")
public class HouseholdController {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(HouseholdController.class);
  private final HouseholdService householdService;
  private static final Logger logger = Logger.getLogger(HouseholdController.class.getName());
  private final UserService userService;

  /**
   * Constructor for injecting the HouseholdService dependency.
   *
   * @param householdService the service layer for household-related operations
   */
  @Autowired
  public HouseholdController(HouseholdService householdService, UserService userService) {
    this.householdService = householdService;
    this.userService = userService;
  }

  /**
   * Endpoint to retrieve the details of the authenticated user's household.
   *
   * @return a ResponseEntity containing the household details
   */
  @GetMapping
  @Operation(summary = "Get details of user's household",
      description = "Retrieves details of the authenticated user's household including all members")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Household details retrieved successfully",
      content = @Content(schema = @Schema(implementation = HouseholdDetailsResponse.class))),
    @ApiResponse(responseCode = "404", description = "Household not found")
  })
  public ResponseEntity<HouseholdDetailsResponse> getMyHouseholdDetails() {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);
    logger.info("Fetching household details for user ID: " + userId);

    HouseholdDetailsResponse householdDetails =
        householdService.getHouseholdDetailsByUserId(userId);
    return ResponseEntity.ok(householdDetails);
  }

  /**
   * Creates a new household and associates it with a user.
   *
   * @param householdRequest the request containing household details
   * @return the created household
   */
  @Operation(summary = "Create a new household",
      description = "Creates a new household and associates it with the authenticated user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Household created successfully",
      content = @Content(schema = @Schema(implementation = HouseholdResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping
  public ResponseEntity<HouseholdResponse> createHousehold(
      @Valid @RequestBody HouseholdRequest householdRequest) {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);
    Household household = HouseholdMapper.INSTANCE.householdRequestToHousehold(householdRequest);
    Household created = householdService.createHousehold(household, userId);
    logger.info("Creating household for userId:" + userId);

    return ResponseEntity.status(HttpStatus.CREATED)
      .body(HouseholdMapper.INSTANCE.householdToHouseholdResponse(created));
  }

  /**
   * Endpoint to request to join a household.
   */
  @PostMapping("/join-request")
  @Operation(summary = "Request to join a household",
      description = "Creates a request for the authenticated user to join a specified household")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Join request created successfully",
      content = @Content(schema = @Schema(implementation = JoinHouseholdRequestResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid household ID"),
    @ApiResponse(responseCode = "404", description = "Household not found")
  })
  public ResponseEntity<JoinHouseholdRequestResponse> requestToJoin(
      @Valid @RequestBody HouseholdJoinRequest request) {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);
    logger.info("Requesting to join household: "
        + request.getHouseholdId() + " for userId: " + userId);

    JoinHouseholdRequest joinRequest = householdService.requestToJoin(
        request.getHouseholdId(), userId);
    return ResponseEntity.ok(JoinRequestMapper.INSTANCE.joinRequestToResponse(joinRequest));
  }

  /**
   * Endpoint to retrieve all join requests for a specific household.
   *
   * @return a ResponseEntity containing a list of JoinHouseholdRequest objects
   */
  @GetMapping("/requests")
  @Operation(summary = "Get all join requests for a household",
      description = "Retrieves all pending join requests for a specific household")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "List of join requests retrieved successfully",
      content = @Content(schema = @Schema(implementation = JoinHouseholdRequestResponse.class))),
    @ApiResponse(responseCode = "403", description =
        "Forbidden - User not authorized for this household"),
    @ApiResponse(responseCode = "404", description = "Household not found")
  })
  public ResponseEntity<List<JoinHouseholdRequestResponse>> getJoinRequests() {
    Long householdId = (long) userService.getCurrentUserHouseholdId();
    logger.info("Retrieving requests for household ID: " + householdId);

    List<JoinHouseholdRequest> joinRequests =
        householdService.getRequestsForHousehold(householdId);
    return ResponseEntity.ok(JoinRequestMapper.INSTANCE
        .joinRequestListToResponseList(joinRequests));
  }

  /**
   * Endpoint to accept a join request and update the user's household association.
   *
   * @param requestId the ID of the join request to accept
   * @return a ResponseEntity with no content
   */
  @Operation(summary = "Accept a join request",
      description = "Accepts a pending join request and updates the user's household association")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Join request accepted successfully"),
    @ApiResponse(responseCode = "403",
      description = "Forbidden - Not authorized to manage this household"),
    @ApiResponse(responseCode = "404", description = "Join request not found")
  })
  @PutMapping("/requests/{requestId}/accept")
  public ResponseEntity<Void> acceptJoinRequest(@PathVariable Long requestId) {
    householdService.acceptJoinRequest(requestId);
    logger.info("Accepting join request: " + requestId);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint to decline a join request by deleting it.
   *
   * @param requestId the ID of the join request to decline
   * @return a ResponseEntity with no content
   */
  @Operation(summary = "Decline a join request",
      description = "Declines and removes a pending join request")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Join request declined successfully"),
    @ApiResponse(responseCode = "403",
      description = "Forbidden - Not authorized to manage this household"),
    @ApiResponse(responseCode = "404", description = "Join request not found")
  })
  @PutMapping("/requests/{requestId}/decline")
  public ResponseEntity<Void> declineJoinRequest(@PathVariable Long requestId) {
    householdService.declineJoinRequest(requestId);
    logger.info("Declining join request: " + requestId);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint to retrieve all members of the authenticated user's household.
   *
   * @return a ResponseEntity containing a list of household members
   */
  @Operation(summary = "Get household members",
      description = "Retrieves all members of the authenticated user's household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description =
          "List of household members retrieved successfully",
          content = @Content(schema = @Schema(implementation =
              GetHouseholdMembersResponse.class))),
      @ApiResponse(responseCode = "500", description =
          "Internal server error"),
      })
  @GetMapping("/members")
  public ResponseEntity<?> getHouseholdMembers() {
    logger.info("Retrieving household members");

    try {
      List<GetHouseholdMembersResponse> responses = householdService.getHouseholdMembers();
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      logger.severe("Error retrieving household members: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error retrieving household members");
    }
  }

  /**
   * Endpoint to retrieve a households readiness status.
   *
   * @return a ResponseEntity containing the readiness status
   */
  @Operation(summary = "Get household readiness status",
      description = "Retrieves the current readiness status and metrics for a specific household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Readiness status retrieved successfully",
          content = @Content(schema = @Schema(implementation = ReadinessResponse.class))),
      @ApiResponse(responseCode = "404", description =
          "Household not found or readiness data unavailable"),
      @ApiResponse(responseCode = "403", description =
          "Forbidden - Not authorized to access this household")
  })
  @GetMapping("/readiness")
  public ResponseEntity<ReadinessResponse> getReadiness() {
    logger.info("Calculating readiness for household");
    try {
      ReadinessResponse readinessResponse = householdService.calculateReadinessForHousehold();
      if (readinessResponse != null) {
        return ResponseEntity.ok(readinessResponse);
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
    } catch (Exception e) {
      logger.severe("Error calculating readiness: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ReadinessResponse(0, 0));
    }
  }
}