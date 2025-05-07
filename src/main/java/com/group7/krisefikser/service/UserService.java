package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.request.ResetPasswordRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.mapper.UserMapper;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Service class for handling user-related operations.
 * This class provides methods for user registration, login, and token management.
 * It implements the UserDetailsService interface for loading user details.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  @Value("${app.frontend.url}")

  private String frontendUrl;

  private final UserRepository userRepo;
  private final JwtUtils jwtUtils;
  private final EmailService emailService;
  private final HouseholdService householdService;
  private final LoginAttemptService loginAttemptService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(),
        new ArrayList<>());
  }

  /**
   * Registers a new user in the system.
   * This method handles the registration process, including creating a household for the user.
   * It also sends a verification email to the user.
   *
   * @param request  the registration request containing user details
   * @return an AuthResponse object containing the result of the registration
   */
  @Transactional
  public AuthResponse registerUser(RegisterRequest request) {
    User user = UserMapper.INSTANCE.registerRequestToUser(request);
    user.setRole(Role.ROLE_NORMAL);
    user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
    Long householdId;
    if (userRepo.findByEmail(user.getEmail()).isPresent()) {
      return new AuthResponse(AuthResponseMessage
          .USER_ALREADY_EXISTS.getMessage(), null, null);
    }

    // When the user is created, we also create a household for them
    // This is for when the user waits for the request to join a household to be accepted
    // This is a temporary solution, in the future the user should be able to create a household
    try {
      householdId = householdService.createHouseholdForUser(user.getName());
    } catch (Exception e) {
      return new AuthResponse(AuthResponseMessage
          .HOUSEHOLD_FAILURE.getMessage() + e.getMessage(), null, null);
    }
    try {
      user.setHouseholdId(householdId);
      userRepo.save(user);
      Optional<User> byEmail = userRepo.findByEmail(user.getEmail());
      String emailVerificationToken = jwtUtils.generateVerificationToken(byEmail.get().getEmail());
      String verificationLink = frontendUrl + "/verify-email?token=" + emailVerificationToken;
      Map<String, String> params = Map.of("verificationLink", verificationLink);
      emailService.sendTemplateMessage(
          byEmail.get().getEmail(), EmailTemplateType.VERIFY_EMAIL, params);

      return new AuthResponse(AuthResponseMessage
          .USER_REGISTERED_SUCCESSFULLY.getMessage(), null, byEmail.get().getRole());
    } catch (Exception e) {
      return new AuthResponse(AuthResponseMessage
          .SAVING_USER_ERROR.getMessage() + e.getMessage(), null, null);
    }
  }

  /**
   * Logs in a user and generates a JWT token.
   * This method verifies the user's credentials and generates a token if valid.
   * It also checks if the user is verified.
   *
   * @param request the login request containing user credentials
   * @return an AuthResponse object containing the result of the login
   */
  public AuthResponse loginUser(LoginRequest request, HttpServletResponse response) {
    String email = request.getEmail();

    Optional<User> userOpt = userRepo.findByEmail(email);

    if (userOpt.isEmpty()) {
      return new AuthResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
    }

    if (!userOpt.get().getVerified()) {
      return new AuthResponse(AuthResponseMessage.USER_NOT_VERIFIED.getMessage(), null, null);
    }

    User user = userOpt.get();

    if (loginAttemptService.isBlocked(user.getEmail())) {
      return new AuthResponse(AuthResponseMessage
          .USER_ACCOUNT_BLOCKED.getMessage(), null, null);
    }

    if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
      loginAttemptService.loginFailed(user.getEmail());
      return new AuthResponse(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null, null);
    }

    loginAttemptService.loginSucceeded(user.getEmail());

    if (user.getRole() == Role.ROLE_ADMIN) {
      try {
        String twoFactorToken = jwtUtils.generate2faToken(user.getId());
        String twoFactorLink = frontendUrl + "/verify-admin?token=" + twoFactorToken;

        emailService.sendTemplateMessage(
            user.getEmail(), EmailTemplateType.ADMIN_VERIFICATION,
            Map.of("loginLink", twoFactorLink));
        return new AuthResponse(
            AuthResponseMessage.TWO_FACTOR_SENT.getMessage(),
            null, null);
      } catch (Exception e) {
        return new AuthResponse(AuthResponseMessage
            .INVALID_EMAIL_FORMAT.getMessage() + e.getMessage(), null, null);
      }
    }

    try {
      String token = jwtUtils.generateToken(user.getId(), user.getRole());
      jwtUtils.setJwtCookie(token, response);
      Date expirationDate = jwtUtils.getExpirationDate(token);

      return new AuthResponse(
          AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(),
          expirationDate,
          user.getRole()
      );
    } catch (Exception e) {
      return new AuthResponse(
          AuthResponseMessage.USER_LOGIN_ERROR.getMessage() + e.getMessage(), null, null);
    }
  }

  /**
   * Verifies the user's email using a token.
   * This method checks if the token is valid and updates the user's verification status.
   *
   * @param token the verification token
   * @return an AuthResponse object containing the result of the verification
   */
  public AuthResponse verifyEmail(String token) {
    try {
      String email = jwtUtils.validateVerificationTokenAndGetEmail(token);
      Optional<User> userOpt = userRepo.findByEmail(email);

      if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setVerified(true);
        userRepo.setVerified(user);
        return new AuthResponse(
            AuthResponseMessage.USER_VERIFIED_SUCCESSFULLY.getMessage(), null, null);
      } else {
        return new AuthResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
      }
    } catch (JwtMissingPropertyException e) {
      return new AuthResponse(AuthResponseMessage.INVALID_TOKEN.getMessage(), null, null);
    }
  }

  /**
   * Resets the user's password using a token.
   * This method checks if the token is valid and updates the user's password.
   *
   * @param request the reset password request containing the token and new password
   * @return an AuthResponse object containing the result of the password reset
   */
  public AuthResponse resetPassword(ResetPasswordRequest request) {
    try {
      String email = jwtUtils.validateResetPasswordTokenAndGetEmail(request.getToken());
      Optional<User> userOpt = userRepo.findByEmail(email);

      if (userOpt.isPresent()) {
        User user = userOpt.get();
        String newPassword = request.getNewPassword();

        if (user.getRole() == Role.ROLE_ADMIN && !PasswordUtil.isStrongPassword(newPassword)) {
          return new AuthResponse(AuthResponseMessage
              .PASSWORD_TOO_WEAK.getMessage(), null, null);
        }

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userRepo.updatePasswordByEmail(user.getEmail(), user.getPassword());
        return new AuthResponse(
            AuthResponseMessage.PASSWORD_RESET_SUCCESS.getMessage(), null, null);
      } else {
        return new AuthResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
      }
    } catch (JwtMissingPropertyException e) {
      return new AuthResponse(AuthResponseMessage.INVALID_TOKEN.getMessage(), null, null);
    }
  }

  /**
   * Sends a password reset link to the user's email.
   * This method generates a reset token and sends it to the user's email.
   * It also checks if the user exists.
   *
   * @param email the user's email address
   */
  public void sendResetPasswordLink(String email) {
    Optional<User> userOpt = userRepo.findByEmail(email);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      String resetToken = jwtUtils.generateResetPasswordToken(user.getEmail());
      String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
      Map<String, String> params = Map.of("resetLink", resetLink);
      emailService.sendTemplateMessage(
          user.getEmail(), EmailTemplateType.PASSWORD_RESET, params);
    } else {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
  }

  /**
   * Gets the current authenticated user's ID from the security context.
   *
   * @return the ID of the authenticated user
   * @throws RuntimeException if no authenticated user is found
   */
  public Long getCurrentUserId() {
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userIdStr == null || userIdStr.isEmpty()) {
      throw new RuntimeException("No authenticated user found");
    }

    return Long.parseLong(userIdStr);
  }

  /**
   * Gets the current authenticated user from the repository.
   *
   * @return the authenticated User entity
   * @throws RuntimeException if the user is not found
   */
  public User getCurrentUser() {
    Long userId = getCurrentUserId();
    return userRepo.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
  }

  /**
   * Gets the household ID of the current authenticated user.
   *
   * @return the household ID of the authenticated user
   * @throws RuntimeException if the user or their household is not found
   */
  public int getCurrentUserHouseholdId() {
    User user = getCurrentUser();

    if (user.getHouseholdId() == null) {
      throw new RuntimeException("User does not belong to any household");
    }

    return user.getHouseholdId().intValue();
  }
}