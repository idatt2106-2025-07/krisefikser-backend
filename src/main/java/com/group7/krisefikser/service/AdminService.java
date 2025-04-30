package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.UuidUtils;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
      if (userRepository.existUserByName(username)) {
        username = "admin" + UuidUtils.generateShortenedUuid();
      } else {
        break;
      }
    }

    if (userRepository.existUserByName(username)) {
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
  public void registerAdmin(RegisterAdminRequest request) throws JwtMissingPropertyException {
    String username = jwtUtils.validateInviteTokenAndGetUsername(request.getToken());
  }
}
