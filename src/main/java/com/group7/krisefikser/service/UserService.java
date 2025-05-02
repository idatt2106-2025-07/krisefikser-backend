package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.mapper.UserMapper;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  private final UserRepository userRepo;
  private final HouseholdRepository householdRepo;
  private final JwtUtils jwtUtils;
  private final EmailService emailService;

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
   * @param response the HTTP response object
   * @return an AuthResponse object containing the result of the registration
   */
  @Transactional
  public AuthResponse registerUser(RegisterRequest request, HttpServletResponse response) {
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
      int counter = 1;
      String baseName = user.getName() + "'s household"
          + UUID.randomUUID().toString().substring(0, 8);
      String householdName = baseName;

      while (householdRepo.existsByName(householdName)) {
        counter++;
        householdName = baseName + " (" + counter + ")";
      }
      // Right now we are creating a household with default values for longitude and latitude
      // In the future, we might want to get these values from the user or use a geolocation service
      double longitude = 0.0;
      double latitude = 0.0;
      householdId = householdRepo.createHousehold(householdName, longitude, latitude);
    } catch (Exception e) {
      return new AuthResponse(AuthResponseMessage
          .HOUSEHOLD_FAILURE.getMessage() + e.getMessage(), null, null);
    }
    try {
      user.setHouseholdId(householdId);
      userRepo.save(user);
      Optional<User> byEmail = userRepo.findByEmail(user.getEmail());
      String emailVerificationToken = jwtUtils.generateToken(byEmail.get().getId(), user.getRole());
      String verificationLink = "https://localhost:5173/verify?token=" + emailVerificationToken;
      Map<String, String> params = Map.of("verificationLink", verificationLink);
      emailService.sendTemplateMessage(
          byEmail.get().getEmail(), EmailTemplateType.VERIFY_EMAIL, params);

      String token = jwtUtils.generateToken(byEmail.get().getId(), user.getRole());
      jwtUtils.setJwtCookie(token, response);
      return new AuthResponse(AuthResponseMessage
          .USER_REGISTERED_SUCCESSFULLY.getMessage(),
          jwtUtils.getExpirationDate(token), byEmail.get().getRole());
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
   * @throws JwtMissingPropertyException if there is an issue with the JWT token
   */
  public AuthResponse loginUser(LoginRequest request, HttpServletResponse response) {
    String email = request.getEmail();

    Optional<User> userOpt = userRepo.findByEmail(email);

    if (userOpt.isEmpty()) {
      return new AuthResponse(AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
    }

    User user = userOpt.get();

    if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
      return new AuthResponse(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null, null);
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
}