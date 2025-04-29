package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepo;
  private final HouseholdRepository householdRepo;
  private final JwtUtils jwtUtils;

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

  public AuthResponse registerUser(RegisterRequest request) {
    String email = request.getEmail();
    String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
    String name = request.getName();
    Long householdId = request.getHouseholdId();

    Optional<User> existingUser = userRepo.findByEmail(email);
    if (existingUser.isPresent()) {
      return new AuthResponse(email, AuthResponseMessage
          .USER_ALREADY_EXISTS.getMessage(), null, null, null);
    }

    if (householdId == null) {
      try {
        // Create a new household using the user's name and default/provided coordinates
        String householdName = name + "'s Household";

        // Default coordinates (you might want to get these from the request instead)
        double longitude = request.getLongitude() != null ? request.getLongitude() : 0.0;
        double latitude = request.getLatitude() != null ? request.getLatitude() : 0.0;

        householdId = householdRepo.createHousehold(householdName, longitude, latitude);
      } catch (Exception e) {
        return new AuthResponse(email, "Failed to create household: " + e.getMessage(), null, null, null);
      }
    }
    Optional<User> newUser;
    try {
      Role role = request.getRole() != null ? Role.valueOf(request.getRole()) : Role.NORMAL;

      newUser = userRepo.save(new User(name, email, hashedPassword, householdId, role));
      String token = jwtUtils.generateToken(newUser.get().getId(), newUser.get().getRole());

      return new AuthResponse(email, AuthResponseMessage
          .USER_REGISTERED_SUCCESSFULLY.getMessage(), token,
          jwtUtils.getExpirationDate(token), newUser.get().getId());
    } catch (Exception e) {
      return new AuthResponse(email, AuthResponseMessage
          .SAVING_USER_ERROR.getMessage() + e.getMessage(), null, null, null);
    }
  }

  public AuthResponse loginUser(LoginRequest request) throws JwtMissingPropertyException {
    String email = request.getEmail();
    System.out.println("Looking up user with email: " + email); // Debug

    Optional<User> userOpt = userRepo.findByEmail(email);
    System.out.println("User found: " + (userOpt.isPresent() ? "Yes" : "No")); // Debug

    if (userOpt.isEmpty()) {
      return new AuthResponse(email, AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null, null);
    }

    if (userOpt.isEmpty()) {
      return new AuthResponse(email, AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null, null);
    }
    User user = userOpt.get();

    if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
      return new AuthResponse(email, AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null, null, null);
    }

    String token = jwtUtils.generateToken(user.getId(), user.getRole());
    Date expirationDate = jwtUtils.getExpirationDate(token);
    Long userId = user.getId();

    return new AuthResponse(
        email,
        AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(),
        token,
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
    return role.equals(Role.ADMIN.toString());
  }

  public AuthResponse refreshToken(String token) throws JwtMissingPropertyException {
    String userId = jwtUtils.validateTokenAndGetUserId(token);
    String role = jwtUtils.validateTokenAndGetRole(token);
    String newToken = jwtUtils.generateToken(Long.parseLong(userId), Role.valueOf(role));
    Date expirationDate = jwtUtils.getExpirationDate(newToken);
    return new AuthResponse(
        userId,
        AuthResponseMessage.TOKEN_REFRESH_ERROR.getMessage(),
        newToken,
        expirationDate,
        Long.parseLong(userId)
    );
  }
}

