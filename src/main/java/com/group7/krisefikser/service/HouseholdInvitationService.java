package com.group7.krisefikser.service;

import static com.group7.krisefikser.enums.EmailTemplateType.HOUSEHOLD_INVITE;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdInvitationRepository;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class for managing household invitations.
 * Handles the creation and acceptance of invitations.
 */
@Service
@RequiredArgsConstructor
public class HouseholdInvitationService {
  private final HouseholdInvitationRepository invitationRepository;
  private final EmailService emailService;
  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final Logger logger = LoggerFactory.getLogger(HouseholdInvitationService.class);


  /**
   * Creates a new household invitation and sends an email to the invitee.
   *
   * @param userId The ID of the user sending the invitation.
   * @param email The email address of the invitee.
   * @return The created HouseholdInvitation object.
   */
  public HouseholdInvitation createInvitation(Long userId, String email) {
    HouseholdInvitation invitation = new HouseholdInvitation();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    invitation.setHouseholdId(user.getHouseholdId());
    invitation.setInvitedByUserId(userId);
    invitation.setInvitedEmail(email);
    invitation = invitationRepository.save(invitation);

    // Send invitation email
    String inviteLink = "http://localhost:5173/invitation/verify?token=" + invitation.getInvitationToken();
    Map<String, String> params = Map.of("inviteLink", inviteLink);
    emailService.sendTemplateMessage(email, HOUSEHOLD_INVITE, params);

    return invitation;
  }

  /**
   * Accepts a household invitation using a token and adds the user to the household.
   *
   * @param token The unique token associated with the invitation.
   * @param userId The ID of the user accepting the invitation.
   * @return The ID of the household the user was added to.
   * @throws RuntimeException if the invitation is not found or has expired.
   */
  public Long acceptInvitation(String token, Long userId) throws JwtMissingPropertyException {
    try {
      String email = jwtUtils.validateInvitationTokenAndGetEmail(token);
      logger.info("Validated invitation token for email: {}", email);

      HouseholdInvitation invitation = invitationRepository.findByToken(token)
          .orElseThrow(() -> new RuntimeException("Invitation not found in database"));

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));
      if (!user.getEmail().equalsIgnoreCase(email)) {
        throw new RuntimeException("You are not authorized to accept this invitation.");
      }

      userRepository.updateUserHousehold(userId, invitation.getHouseholdId());

      invitationRepository.delete(invitation.getId());

      return invitation.getHouseholdId();
    } catch (JWTVerificationException | JwtMissingPropertyException e) {
      logger.error("Failed to validate invitation token: {}", e.getMessage());
      throw new RuntimeException("Invalid or expired invitation token", e);
    }
  }

  /**
   * Verifies a household invitation using a token.
   *
   * @param token The unique token associated with the invitation.
   * @return The HouseholdInvitation object if found and valid.
   * @throws RuntimeException if the invitation is not found or has expired.
   */
  public HouseholdInvitation verifyInvitation(String token) {
    return invitationRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Invitation not found or expired"));
  }
}