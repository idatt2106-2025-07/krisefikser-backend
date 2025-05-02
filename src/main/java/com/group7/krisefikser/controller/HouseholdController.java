package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.HouseholdJoinRequest;
import com.group7.krisefikser.dto.request.HouseholdRequest;
import com.group7.krisefikser.dto.response.HouseholdResponse;
import com.group7.krisefikser.mapper.HouseholdMapper;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.service.HouseholdService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;
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
public class HouseholdController {
  private final HouseholdService householdService;
  private static final Logger logger = Logger.getLogger(HouseholdController.class.getName());

  /**
   * Constructor for injecting the HouseholdService dependency.
   *
   * @param householdService the service layer for household-related operations
   */
  @Autowired
  public HouseholdController(HouseholdService householdService) {
    this.householdService = householdService;
  }

  /**
   * Creates a new household and associates it with a user.
   *
   * @param householdRequest the request containing household details
   * @return the created household
   */
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
  public ResponseEntity<JoinHouseholdRequest> requestToJoin(
      @RequestBody HouseholdJoinRequest request) {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    Long userId = Long.parseLong(userIdStr);
    logger.info("Requesting to join household:" + request.getHouseholdId()
        + "for userId:" + userId);

    JoinHouseholdRequest joinRequest =
        householdService.requestToJoin(request.getHouseholdId(), userId);
    return ResponseEntity.ok(joinRequest);
  }

  /**
   * Endpoint to retrieve all join requests for a specific household.
   *
   * @param householdId the ID of the household
   * @return a ResponseEntity containing a list of JoinHouseholdRequest objects
   */
  @GetMapping("/{householdId}/requests")
  public ResponseEntity<List<JoinHouseholdRequest>> getJoinRequests(
      @PathVariable Long householdId) {
    logger.info("Retrieving requests for household ID: + " + householdId);
    return ResponseEntity.ok(householdService.getRequestsForHousehold(householdId));
  }

  /**
   * Endpoint to accept a join request and update the user's household association.
   *
   * @param requestId the ID of the join request to accept
   * @return a ResponseEntity with no content
   */
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
  @PutMapping("/requests/{requestId}/decline")
  public ResponseEntity<Void> declineJoinRequest(@PathVariable Long requestId) {
    householdService.declineJoinRequest(requestId);
    logger.info("Declining join request: " + requestId);
    return ResponseEntity.ok().build();
  }
}