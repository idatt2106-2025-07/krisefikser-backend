package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.JoinHouseholdRequest;
import com.group7.krisefikser.dto.response.HouseholdDetailsResponse;
import com.group7.krisefikser.dto.response.HouseholdMemberResponse;
import com.group7.krisefikser.dto.response.NonUserMemberResponse;
import com.group7.krisefikser.exception.ResourceNotFoundException;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.JoinHouseholdRequestRepo;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.UuidUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing household-related operations.
 * Provides methods for creating households, handling join requests,
 * and managing household memberships.
 */
@Service
@RequiredArgsConstructor
public class HouseholdService {
  private final JoinHouseholdRequestRepo joinRequestRepo;
  private final UserRepository userRepository;
  private final HouseholdRepository householdRepository;


  /**
   * Creates a household for the user with a unique name.
   * The household name is generated based on the user's name and a UUID.
   * If a household with the same name already exists, it appends a counter to the name.
   *
   * @param userName The name of the user for whom the household is being created.
   * @return The ID of the created household.
   */
  public Long createHouseholdForUser(String userName) {
    Long householdId;
    int counter = 1;
    String baseName = userName + "'s household"
        + UuidUtils.generateShortenedUuid();
    String householdName = baseName;

    while (householdRepository.existsByName(householdName)) {
      counter++;
      householdName = baseName + " (" + counter + ")";
    }
    // Right now we are creating a household with default values for longitude and latitude
    // In the future, we might want to get these values from the user or use a geolocation service
    double longitude = 0.0;
    double latitude = 0.0;
    return householdRepository.createHousehold(householdName, longitude, latitude);
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
    Household saved = householdRepository.save(household);
    userRepository.updateUserHousehold(userId, saved.getId());
    return saved;
  }

  /**
   * Creates a new household with the specified details.
   * Persists the household using the provided name, longitude, and latitude.
   * This operation is transactional.
   *
   * @param household the Household object containing the name, longitude, and latitude
   * @return the ID of the created household
   */
  @Transactional
  public Long createHousehold(Household household) {
    return householdRepository
        .createHousehold(household.getName(), household.getLongitude(), household.getLatitude());
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
    userRepository.updateUserHousehold(request.getUserId(), request.getHouseholdId());

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

  /**
   * Retrieves the details of a user's household including all members.
   *
   * @param userId the ID of the user
   * @return a HouseholdDetailsResponse containing household information and its members
   * @throws ResourceNotFoundException if the household or user is not found
   */
  public HouseholdDetailsResponse getHouseholdDetailsByUserId(Long userId) {
    // Get the user's household ID
    Long householdId = userRepository.findHouseholdIdByUserId(userId);

    if (householdId == null) {
      throw new ResourceNotFoundException("User is not associated with any household");
    }

    // Get household details
    Household household = householdRepository.getHouseholdById(householdId)
        .orElseThrow(() -> new ResourceNotFoundException("Household not found"));

    // Get household members and non-user members
    List<HouseholdMemberResponse> members =
        householdRepository.findMembersByHouseholdId(householdId);
    List<NonUserMemberResponse> nonUserMembers =
        householdRepository.findNonUserMembersByHouseholdId(householdId);

    // Create response
    HouseholdDetailsResponse response = new HouseholdDetailsResponse();
    response.setId(household.getId());
    response.setName(household.getName());
    response.setLongitude(household.getLongitude());
    response.setLatitude(household.getLatitude());
    response.setMembers(members);
    response.setNonUserMembers(nonUserMembers);

    return response;
  }
}
