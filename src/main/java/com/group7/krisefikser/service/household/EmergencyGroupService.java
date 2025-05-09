package com.group7.krisefikser.service.household;


import com.group7.krisefikser.dto.request.household.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.household.EmergencyGroupInvitationResponse;
import com.group7.krisefikser.dto.response.household.EmergencyGroupResponse;
import com.group7.krisefikser.mapper.household.EmergencyGroupMapper;
import com.group7.krisefikser.model.household.EmergencyGroup;
import com.group7.krisefikser.model.household.EmergencyGroupInvitation;
import com.group7.krisefikser.model.household.Household;
import com.group7.krisefikser.repository.household.EmergencyGroupInvitationsRepo;
import com.group7.krisefikser.repository.household.EmergencyGroupRepo;
import com.group7.krisefikser.repository.household.HouseholdRepository;
import com.group7.krisefikser.repository.user.UserRepository;
import java.util.List;
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
  @Transactional
  public EmergencyGroupResponse addEmergencyGroup(EmergencyGroupRequest request) {
    try {
      EmergencyGroup group = EmergencyGroupMapper.INSTANCE
              .emergencyGroupRequestToEntity(request);
      emergencyGroupRepo.addEmergencyGroup(group);
      getHouseholdIdForCurrentUser();
      householdRepository.addHouseholdToGroup(
              getHouseholdIdForCurrentUser(),
              group.getId()
      );
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
    Household householdToInvite = householdRepository.getHouseholdByName(householdName)
            .orElseThrow(() -> new NoSuchElementException(
                    "Household with name '" + householdName + "' not found.")
            );
    Long oldGroupId = householdToInvite.getEmergencyGroupId();
    if (oldGroupId != null && oldGroupId == getGroupIdForCurrentUser()) {
      throw new IllegalArgumentException("The household is already in the group.");
    }

    if (emergencyGroupInvitationsRepo.isInvitedToGroup(
            householdToInvite.getId(),
            getGroupIdForCurrentUser()
    )) {
      throw new IllegalArgumentException("Household is already invited to this group.");
    }

    emergencyGroupInvitationsRepo.addEmergencyGroupInvitation(new EmergencyGroupInvitation(
                    null,
                    householdToInvite.getId(),
                    getGroupIdForCurrentUser(),
                    null
            )
    );

  }

  /**
   * Retrieves the ID of the group associated with the current user's household.
   *
   * @return the ID of the group associated with the user's household
   * @throws NoSuchElementException if user has no household or household has no group
   */
  private long getGroupIdForCurrentUser() {
    long householdId = getHouseholdIdForCurrentUser();
    Long emergencyGroupId = householdRepository.getEmergencyIdByHouseholdId(householdId);

    if (emergencyGroupId == null) {
      throw new IllegalArgumentException("Your household is not part of any emergency group");
    }

    return emergencyGroupId;
  }

  /**
   * Answers an emergency group invitation by accepting or declining it.
   *
   * @param groupId the ID of the emergency group
   * @param accept  true to accept the invitation, false to decline
   */
  @Transactional
  public void answerEmergencyGroupInvitation(Long groupId, boolean accept) {
    long householdId = getHouseholdIdForCurrentUser();
    if (!emergencyGroupInvitationsRepo.isInvitedToGroup(householdId, groupId)) {
      throw new IllegalArgumentException("Household is not invited to this group.");
    }

    if (accept) {
      householdRepository.addHouseholdToGroup(householdId, groupId);
    }

    emergencyGroupInvitationsRepo.deleteEmergencyGroupInvitation(householdId, groupId);
  }

  /**
   * Retrieves the ID of the household associated with the current user.
   *
   * @return the ID of the household associated with the current user
   */
  private long getHouseholdIdForCurrentUser() {
    long userId = Long.parseLong(SecurityContextHolder.getContext()
            .getAuthentication().getName());
    return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found."))
            .getHouseholdId();
  }

  /**
   * Retrieves all emergency group invitations for the current user's household.
   *
   * @return a list of EmergencyGroupInvitationResponse objects
   */
  public List<EmergencyGroupInvitationResponse> getEmergencyGroupInvitationsForCurrentUser() {
    long householdId = getHouseholdIdForCurrentUser();
    List<EmergencyGroupInvitation> invitations = emergencyGroupInvitationsRepo
            .getInvitationsByHouseholdId(householdId);

    return invitations.stream()
            .map(invitation -> new EmergencyGroupInvitationResponse(
                    invitation.getId(),
                    invitation.getHouseholdId(),
                    invitation.getGroupId(),
                    invitation.getCreatedAt().toString(),
                    emergencyGroupRepo.getEmergencyGroupById(invitation.getGroupId()).getName()
            ))
            .toList();
  }

  /**
   * Retrieves the emergency group ID associated with a household.
   *
   * @param householdId the ID of the household
   * @return the ID of the associated emergency group
   * @throws NoSuchElementException if the household is not found or has no associated group
   */
  public Long getEmergencyGroupIdByHouseholdId(Long householdId) {
    Long emergencyGroupId = householdRepository.getEmergencyIdByHouseholdId(householdId);
    if (emergencyGroupId == null) {
      throw new NoSuchElementException("No emergency group found for household");
    }
    return emergencyGroupId;
  }
}
