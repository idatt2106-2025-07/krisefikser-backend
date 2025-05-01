package com.group7.krisefikser.controller;

import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.service.HouseholdService;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * Endpoint to create a new household and associate it with a user.
   *
   * @param household the Household object containing household details
   * @param userId the ID of the user creating the household
   * @return a ResponseEntity containing the created Household object
   */
  @PostMapping("/create")
  public ResponseEntity<Household> createHousehold(@RequestBody Household household,
                                                   @RequestParam Long userId) {
    logger.info("Creating household for userId: " + userId);
    return ResponseEntity.ok(householdService.createHousehold(household, userId));
  }

  /**
   * Endpoint to create a request for a user to join a household.
   *
   * @param householdId the ID of the household to join
   * @param userId the ID of the user making the request
   * @return a ResponseEntity containing the saved JoinHouseholdRequest object
   */
  @PostMapping("/join-request")
  public ResponseEntity<JoinHouseholdRequest> requestToJoin(@RequestParam Long householdId,
                                                            @RequestParam Long userId) {
    logger.info("User " + userId + " requesting to join household " + householdId);
    return ResponseEntity.ok(householdService.requestToJoin(householdId, userId));
  }

  /**
   * Endpoint to retrieve all join requests for a specific household.
   *
   * @param householdId the ID of the household
   * @return a ResponseEntity containing a list of JoinHouseholdRequest objects
   */
  @GetMapping("/{householdId}/requests")
  public ResponseEntity<List<JoinHouseholdRequest>> getRequests(@PathVariable Long householdId) {
    logger.info("Retrieving request for household: " + householdId);
    return ResponseEntity.ok(householdService.getRequestsForHousehold(householdId));
  }

  /**
   * Endpoint to accept a join request and update the user's household association.
   *
   * @param requestId the ID of the join request to accept
   * @return a ResponseEntity with no content
   */
  @PutMapping("/requests/{requestId}/accept")
  public ResponseEntity<Void> acceptRequest(@PathVariable Long requestId) {
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
  public ResponseEntity<Void> declineRequest(@PathVariable Long requestId) {
    householdService.declineJoinRequest(requestId);
    logger.info("declining join request: " + requestId);
    return ResponseEntity.ok().build();
  }
}