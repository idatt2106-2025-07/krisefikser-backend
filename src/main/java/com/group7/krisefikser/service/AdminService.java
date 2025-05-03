package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import com.group7.krisefikser.utils.UuidUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling admin-related operations.
 * The role of this service is to manage the business logic related to
 * admin operations, such as inviting and registering admins.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

  private final EmailService emailService;

  private final JwtUtils jwtUtils;

  private final UserRepository userRepository;

  private final HouseholdService householdService;

  /**
   * Invites an admin by generating a jwt invite token and sending an email with the invite link.
   *
   * @param request The request containing the email of the admin to be invited.
   * @throws JwtMissingPropertyException if there is an issue with the JWT properties.
   */
  public void inviteAdmin(InviteAdminRequest request)
      throws JwtMissingPropertyException, UsernameGenerationException {

    String username = "admin" + UuidUtils.generateShortenedUuid();

    for (int i = 0; i < 15; i++) {
      if (userRepository.existAdminByUsername(username)) {
        username = "admin" + UuidUtils.generateShortenedUuid();
      } else {
        break;
      }
    }

    if (userRepository.existAdminByUsername(username)) {
      throw new UsernameGenerationException("Failed to generate a unique username");
    }

    String inviteToken = jwtUtils.generateInviteToken(username);

    String inviteLink = "https://localhost:5173/invite?token=" + inviteToken;

    emailService.sendTemplateMessage(
        request.getEmail(),
        EmailTemplateType.ADMIN_INVITE,
        Map.of("inviteLink", inviteLink)
    );
  }

  /**
   * Registers an admin by validating the invite token and creating a new admin account.
   *
   * @param request The request containing the invite token and other registration details.
   * @throws JwtMissingPropertyException if there is an issue with the JWT properties.
   */
  @Transactional
  public void registerAdmin(RegisterAdminRequest request)
      throws JwtMissingPropertyException, UsernameGenerationException {
    String username = jwtUtils.validateInviteTokenAndGetUsername(request.getToken());
    User user = new User();
    user.setEmail(request.getEmail());
    user.setName(username);
    user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
    user.setRole(Role.ROLE_ADMIN);

    if (userRepository.existAdminByUsername(username)) {
      throw new UsernameGenerationException("Username already taken");
    }
    Long householdId = householdService.createHouseholdForUser(username);
    user.setHouseholdId(householdId);
    userRepository.save(user);
  }

  /**
   * Verifies the two-factor authentication token and generates a JWT token for the admin.
   *
   * @param twoFactorToken The two-factor authentication token.
   * @param response      The HTTP response object to set the JWT cookie.
   * @throws JwtMissingPropertyException if there is an issue with the JWT properties.
   */
  public void verifyTwoFactor(String twoFactorToken, HttpServletResponse response)
      throws JwtMissingPropertyException {
    String userId = jwtUtils.validate2faTokenAndGetUserId(twoFactorToken);
    String token = jwtUtils.generateToken(Long.parseLong(userId), Role.ROLE_ADMIN);
    jwtUtils.setJwtCookie(token, response);
  }
}