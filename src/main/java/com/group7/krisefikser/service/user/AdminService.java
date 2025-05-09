package com.group7.krisefikser.service.user;

import com.group7.krisefikser.dto.request.user.RegisterAdminRequest;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.model.user.User;
import com.group7.krisefikser.repository.user.UserRepository;
import com.group7.krisefikser.service.household.HouseholdService;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import jakarta.servlet.http.HttpServletResponse;
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

  private final JwtUtils jwtUtils;

  private final UserRepository userRepository;

  private final HouseholdService householdService;

  /**
   * Registers an admin by validating the invite token and creating a new admin account.
   *
   * @param request The request containing the invite token and other registration details.
   * @throws JwtMissingPropertyException if there is an issue with the JWT properties.
   */
  @Transactional
  public void registerAdmin(RegisterAdminRequest request)
      throws JwtMissingPropertyException, UsernameGenerationException {
    String username = jwtUtils.validateInviteAdminTokenAndGetUsername(request.getToken());
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
    user.setVerified(true);
    userRepository.setVerified(user);
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