package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.mapper.SuperAdminMapper;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.UuidUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling super admin-related operations.
 * This class provides methods for inviting admins,
 * retrieving all admins,
 * deleting admins,
 * and sending reset password emails to admins.
 */
@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final JwtUtils jwtUtils;
  private final EmailService emailService;

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
   * Retrieves all admins from the repository.
   * This method fetches the list of admins
   * from the user repository
   * and maps them to SuperAdminResponse objects.
   *
   * @return a list of SuperAdminResponse objects containing details of the admins
   */
  public List<SuperAdminResponse> getAllAdmins() {
    try {
      List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
      if (admins.isEmpty()) {
        throw new IllegalArgumentException("No admins found");
      }
      return SuperAdminMapper
          .INSTANCE.userToSuperAdminResponse(admins);
    } catch (Exception e) {
      throw new RuntimeException("Error fetching admins: " + e.getMessage());
    }
  }

  /**
   * Deletes an admin by their ID.
   * This method checks if the user exists
   * and if they have the admin role before deleting them.
   * If the user is not found or does not have the admin role,
   * an exception is thrown.
   *
   * @param adminId the ID of the admin to be deleted
   */
  public void deleteAdmin(Long adminId) {
    try {
      Optional<User> user = userRepository.findById(adminId);
      if (user.isPresent()) {
        if (!user.get().getRole().equals(Role.ROLE_ADMIN)) {
          throw new IllegalArgumentException("User is not an admin");
        }
        userRepository.deleteById(adminId);
      } else {
        throw new IllegalArgumentException("User not found");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error deleting admin: " + e.getMessage());
    }
  }

  /**
   * Sends a reset password email to an admin.
   * This method checks if the user exists
   * and if they have the admin role before sending the email.
   * If the user is not found or does not have the admin role,
   * an exception is thrown.
   *
   * @param email the email of the admin to send the reset password email to
   */
  public void sendResetPasswordEmailToAdmin(String email) {
    try {
      Optional<User> user = userRepository.findByEmail(email);
      if (user.isPresent()) {
        if (!user.get().getRole().equals(Role.ROLE_ADMIN)) {
          throw new IllegalArgumentException("User is not an admin");
        }
        userService.sendResetPasswordLink(email);
      } else {
        throw new IllegalArgumentException("User not found");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error sending reset password email: " + e.getMessage());
    }
  }
}
