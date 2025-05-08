package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.GetHouseholdMembersResponse;
import com.group7.krisefikser.dto.request.ReadinessRequest;
import com.group7.krisefikser.dto.response.ReadinessResponse;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.Item;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.model.NonUserMember;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.model.StorageItem;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.ItemRepo;
import com.group7.krisefikser.repository.JoinHouseholdRequestRepo;
import com.group7.krisefikser.repository.StorageItemRepo;
import com.group7.krisefikser.repository.NonUserMemberRepository;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.UuidUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final StorageItemRepo storageItemRepo;
  private final ItemRepo itemRepo;
  private final NonUserMemberRepository nonUserMemberRepository;


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
   * Retrieves a household by its ID.
   *
   * @param id the ID of the household
   * @return the Household object if found, null otherwise
   */
  public Household getHouseholdById(Long id) {
    return householdRepository.getHouseholdById(id).orElse(null);
  }

  /**
   * Retrieves all household members for the current user.
   *
   * @return a list of GetHouseholdMembersResponse objects representing the household members
   */
  public List<GetHouseholdMembersResponse> getHouseholdMembers() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    long householdId = userRepository.findById(Long.parseLong(userId)).get().getHouseholdId();
    List<User> users = userRepository.getUsersByHouseholdId(householdId);
    List<NonUserMember> nonUserMembers =
        nonUserMemberRepository.getNonUserMembersByHousehold(householdId);
    List<GetHouseholdMembersResponse> responses = new java.util.ArrayList<>(users.stream()
        .map(user -> new GetHouseholdMembersResponse(user.getId(), user.getName(),
            "USER"))
        .toList());
    responses.addAll(nonUserMembers.stream()
        .map(nonUserMember -> new GetHouseholdMembersResponse(nonUserMember.getId(),
            nonUserMember.getName(), nonUserMember.getType().toString()))
        .toList());
    return responses;
  }

  /**
   * Calculates the readiness of a household based on its storage items, calories and user count.
   *
   * @param request the ReadinessRequest object containing household ID
   * @return a ReadinessResponse object containing the calculated readiness
   */
  public ReadinessResponse calculateReadinessForHousehold(ReadinessRequest request) {
    Household household = householdRepository.getHouseholdById(
        request.getHouseholdId()).orElse(null);
    if (household == null) {
      return null;
    }
    List<User> users = userRepository.getUsersByHouseholdId(household.getId());
    // Make list of non-user members (others) when non-user members are added
//    List<NonUserMember> others = nonUserMemberRepo.getNonUserMembersByHouseholdId();

    List<StorageItem> storageItems = storageItemRepo.getAllStorageItems(
        household.getId().intValue());

    int totalCalories = 0;
    double totalLiters = 0.0;

    for (StorageItem si : storageItems) {
      if (si.getExpirationDate().isAfter(LocalDateTime.ofInstant(
          Instant.now(), ZoneId.systemDefault()))) {
        Item item = itemRepo.findById(si.getItemId()).orElse(null);
        totalCalories += item.getCalories() * si.getQuantity();

        if ("L".equalsIgnoreCase(item.getUnit()) &&
            "drink".equalsIgnoreCase(String.valueOf(item.getType()))) {
          totalLiters += si.getQuantity();
        }
      }
    }

    int people = users.size();
//    double nonUserFactor = 0.75;
//    double totalPeople = people + others.size() * nonUserFactor;

    double dailyCalories = people * 2000;
    double dailyLiters = people * 2.0;

    double calorieDays = totalCalories / dailyCalories;
    double waterDays = totalLiters / dailyLiters;

    double minDays = Math.min(calorieDays, waterDays);
    int fullDays = (int) minDays;
    int hours = (int) ((minDays - fullDays) * 24);

    return new ReadinessResponse(fullDays, hours);
  }
}
