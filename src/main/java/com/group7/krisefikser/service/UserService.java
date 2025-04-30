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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
        new ArrayList<>());
  }

  @Transactional
  public AuthResponse registerUser(RegisterRequest request, HttpServletResponse response) {
    User user = UserMapper.INSTANCE.registerRequestToUser(request);
    user.setRole(Role.ROLE_NORMAL);
    user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
    Long householdId;
    if (userRepo.findByEmail(user.getEmail()).isPresent()) {
      return new AuthResponse(user.getEmail(), AuthResponseMessage
          .USER_ALREADY_EXISTS.getMessage(), null, null);
    }
    /**
     * When the user is created, we also create a household for them
     * This is for when the user waits for the request to join a household to be accepted
     * This is a temporary solution, in the future the user should be able to create a household
     */
    try {
      String householdName = request.getHouseholdName();
      // Right now we are creating a household with default values for longitude and latitude
      // In the future, we might want to get these values from the user or use a geolocation service
      double longitude = 0.0;
      double latitude = 0.0;
      householdId = householdRepo.createHousehold(householdName, longitude, latitude);
    } catch (Exception e) {
      return new AuthResponse(user.getEmail(), AuthResponseMessage
          .HOUSEHOLD_FAILURE.getMessage() + e.getMessage(), null, null);
    }
    try {
      user.setHouseholdId(householdId);
      userRepo.save(user);
      Optional<User> byEmail = userRepo.findByEmail(user.getEmail());
      String emailVerificationToken = jwtUtils.generateEmailVerificationToken(byEmail.get().getEmail());

      String verificationLink = "https://localhost:5173/verify?token=" + emailVerificationToken;
      Map<String, String> params = Map.of("verificationLink", verificationLink);
      emailService.sendTemplateMessage(byEmail.get().getEmail(), EmailTemplateType.VERIFY_EMAIL, params);

      String token = jwtUtils.generateToken(byEmail.get().getId(), user.getRole());
      jwtUtils.setJwtCookie(token, response);
      return new AuthResponse(user.getEmail(), AuthResponseMessage
          .USER_REGISTERED_SUCCESSFULLY.getMessage(),
          jwtUtils.getExpirationDate(token), byEmail.get().getId());
    } catch (Exception e) {
      return new AuthResponse(user.getEmail(), AuthResponseMessage
          .SAVING_USER_ERROR.getMessage() + e.getMessage(), null, null);
    }
  }

  public AuthResponse loginUser(LoginRequest request) throws JwtMissingPropertyException {
    String email = request.getEmail();
    System.out.println("Looking up user with email: " + email); // Debug

    Optional<User> userOpt = userRepo.findByEmail(email);
    System.out.println("User found: " + (userOpt.isPresent() ? "Yes" : "No")); // Debug

    if (userOpt.isEmpty()) {
      return new AuthResponse(email, AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
    }

    if (userOpt.isEmpty()) {
      return new AuthResponse(email, AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
    }
    User user = userOpt.get();

    if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
      return new AuthResponse(email, AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null, null);
    }

    String token = jwtUtils.generateToken(user.getId(), user.getRole());
    Date expirationDate = jwtUtils.getExpirationDate(token);
    Long userId = user.getId();

    return new AuthResponse(
        email,
        AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(),
        expirationDate,
        userId
    );
  }

  public boolean userExists(Long id) {
    return userRepo.findById(id).isPresent();
  }

  public boolean validateUserIdMatchesToken(String token, Long userId) throws JwtMissingPropertyException {
    String tokenUserId = jwtUtils.validateTokenAndGetUserId(token);
    return tokenUserId.equals(String.valueOf(userId));
  }

  public boolean validateAdmin(String token) throws JwtMissingPropertyException {
    String role = jwtUtils.validateTokenAndGetRole(token);
    return role.equals(Role.ROLE_ADMIN.toString());
  }

  public AuthResponse refreshToken(String token) throws JwtMissingPropertyException {
    String userId = jwtUtils.validateTokenAndGetUserId(token);
    String role = jwtUtils.validateTokenAndGetRole(token);
    String newToken = jwtUtils.generateToken(Long.parseLong(userId), Role.valueOf(role));
    Date expirationDate = jwtUtils.getExpirationDate(newToken);
    return new AuthResponse(
        userId,
        AuthResponseMessage.TOKEN_REFRESH_ERROR.getMessage(),
        expirationDate,
        Long.parseLong(userId)
    );
  }
}

