package com.group7.krisefikser.service;


import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.mapper.EmergencyGroupMapper;
import com.group7.krisefikser.model.EmergencyGroup;
import com.group7.krisefikser.model.EmergencyGroupInvitation;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.repository.EmergencyGroupInvitationsRepo;
import com.group7.krisefikser.repository.EmergencyGroupRepo;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling operations related to emergency groups.
 * The role of this service is to manage the business logic related to
 * emergency groups, such as retrieving and adding.
 */
@Service
@RequiredArgsConstructor
public class EmergencyGroupService {
  private final EmergencyGroupRepo emergencyGroupRepo;
  private final EmergencyGroupInvitationsRepo emergencyGroupInvitationsRepo;
  private final UserRepository userRepository;
  private final HouseholdRepository householdRepository;

  /**
   * Retrieves the EmergencyGroup object with the specified ID from the repository.
   *
   * @param id the ID of the EmergencyGroup to retrieve
   * @return the EmergencyGroupResponse object with the specified ID
   */
  public EmergencyGroupResponse getEmergencyGroupById(Long id) {
    try {
      EmergencyGroup group = emergencyGroupRepo.getEmergencyGroupById(id);
      return EmergencyGroupMapper.INSTANCE.emergencyGroupToResponse(group);
    } catch (EmptyResultDataAccessException e) {
      throw new NoSuchElementException("Emergency group with ID " + id + " not found.");
    }
  }

  /**
   * Adds a new EmergencyGroup to the repository.
   *
   * @param request the EmergencyGroup object to add
   * @return the EmergencyGroupResponse object representing the added group
   */
  public EmergencyGroupResponse addEmergencyGroup(EmergencyGroupRequest request) {
    try {
      EmergencyGroup group = EmergencyGroupMapper.INSTANCE
              .emergencyGroupRequestToEntity(request);
      emergencyGroupRepo.addEmergencyGroup(group);
      return EmergencyGroupMapper.INSTANCE.emergencyGroupToResponse(group);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Failed to add emergency group. Name already taken.");
    }
  }

  /**
   * Invites a household to an emergency group by its name.
   *
   * @param householdName the name of the household to invite
   */
  public void inviteHouseholdByName(String householdName) {
    long userId = Long.parseLong(SecurityContextHolder.getContext()
            .getAuthentication().getName());

    Household householdToInvite = householdRepository.getHouseholdByName(householdName)
            .orElseThrow(() -> new NoSuchElementException(
                    "Household with name '" + householdName + "' not found.")
            );
    Long oldGroupId = householdToInvite.getEmergencyGroupId();
    if (oldGroupId != null && oldGroupId == getGroupIdByUserId(userId)) {
      throw new IllegalArgumentException("The household is already in the group.");
    }

    if (emergencyGroupInvitationsRepo.isInvitedToGroup(
            householdToInvite.getId(),
            getGroupIdByUserId(userId)
    )) {
      throw new IllegalArgumentException("Household is already invited to this group.");
    }

    emergencyGroupInvitationsRepo.addEmergencyGroupInvitation(new EmergencyGroupInvitation(
                    null,
                    householdToInvite.getId(),
                    getGroupIdByUserId(userId),
                    null
            )
    );

  }

  /**
   * Retrieves the ID of the group associated with the users household.
   *
   * @param userId the ID of the user
   * @return the ID of the group associated with the user's household
   */
  private long getGroupIdByUserId(Long userId) {
    long householdId = userRepository.findById(userId).orElseThrow(
            () -> new NoSuchElementException("User not found.")
    ).getHouseholdId();

    return householdRepository.getHouseholdById(householdId)
            .orElseThrow(() -> new NoSuchElementException("The requesting user"
                    + "is not part of a household."))
            .getId();
  }

  /**
   * Answers an emergency group invitation by accepting or declining it.
   *
   * @param groupId the ID of the emergency group
   * @param accept  true to accept the invitation, false to decline
   */
  @Transactional
  public void answerEmergencyGroupInvitation(Long groupId, boolean accept) {
    long userId = Long.parseLong(SecurityContextHolder.getContext()
            .getAuthentication().getName());
    long householdId = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found."))
            .getHouseholdId();

    if (!emergencyGroupInvitationsRepo.isInvitedToGroup(householdId, groupId)) {
      throw new IllegalArgumentException("Household is not invited to this group.");
    }

    if (accept) {
      householdRepository.addHouseholdToGroup(householdId, groupId);
    }

    emergencyGroupInvitationsRepo.deleteEmergencyGroupInvitation(householdId, groupId);
  }
}
