package com.group7.krisefikser.service;

import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.repository.JoinHouseholdRequestRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing household-related operations.
 * Provides methods for creating households, handling join requests,
 * and managing household memberships.
 */
@Service
public class HouseholdService {
  private final JdbcTemplate jdbcTemplate;
  private final JoinHouseholdRequestRepo joinRequestRepo;

  /**
   * Constructor for injecting dependencies.
   *
   * @param jdbcTemplate the JdbcTemplate instance used for database operations
   * @param joinHouseholdRequestRepo the repository for managing join household requests
   */
  @Autowired
  public HouseholdService(
      JdbcTemplate jdbcTemplate, JoinHouseholdRequestRepo joinHouseholdRequestRepo) {
    this.jdbcTemplate = jdbcTemplate;
    this.joinRequestRepo = joinHouseholdRequestRepo;
  }

  /**
   * Creates a new household and associates it with a user.
   *
   * @param household the Household object containing household details
   * @param userId the ID of the user creating the household
   * @return the created Household object with the generated ID
   */
  @Transactional
  public Household createHousehold(Household household, Long userId) {
    SimpleJdbcInsert householdInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("households")
        .usingGeneratedKeyColumns("id");

    Map<String, Object> params = new HashMap<>();
    params.put("name", household.getName());
    params.put("longitude", household.getLongitude());
    params.put("latitude", household.getLatitude());

    Number householdId = householdInsert.executeAndReturnKey(params);
    household.setId(householdId.longValue());

    jdbcTemplate.update(
        "UPDATE users SET household_id = ? WHERE id = ?",
        household.getId(), userId);

    return household;
  }

  /**
   * Creates a request for a user to join a household.
   *
   * @param householdId the ID of the household to join
   * @param userId the ID of the user making the request
   * @return the saved JoinHouseholdRequest object
   */
  @Transactional
  public JoinHouseholdRequest requestToJoin(Long householdId, Long userId) {
    JoinHouseholdRequest request = new JoinHouseholdRequest();
    request.setHouseholdId(householdId);
    request.setUserId(userId);
    return joinRequestRepo.save(request);
  }

  /**
   * Accepts a join request and updates the user's household association.
   *
   * @param requestId the ID of the join request to accept
   */
  @Transactional
  public void acceptJoinRequest(Long requestId) {
    JoinHouseholdRequest request = joinRequestRepo.findById(requestId);

    // Update user's household ID
    jdbcTemplate.update(
        "UPDATE users SET household_id = ? WHERE id = ?",
        request.getHouseholdId(), request.getUserId());

    // Delete the request after accepting
    joinRequestRepo.deleteById(requestId);
  }

  /**
   * Declines a join request by deleting it.
   *
   * @param requestId the ID of the join request to decline
   */
  @Transactional
  public void declineJoinRequest(Long requestId) {
    joinRequestRepo.deleteById(requestId);
  }

  /**
   * Retrieves all join requests for a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of JoinHouseholdRequest objects associated with the household
   */
  public List<JoinHouseholdRequest> getRequestsForHousehold(Long householdId) {
    return joinRequestRepo.findByHouseholdId(householdId);
  }
}