package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
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

  @Transactional
  public AuthResponse registerUser(RegisterRequest request, HttpServletResponse response) {
    User newUser = UserMapper.INSTANCE.registerRequestToUser(request);
    newUser.setRole(Role.NORMAL);
    newUser.setPassword(PasswordUtil.hashPassword(request.getPassword()));
//    String email = request.getEmail();
//    String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
//    String name = request.getName();
//    Long householdId;
//
//    Optional<User> existingUser = userRepo.findByEmail(email);
//    if (existingUser.isPresent()) {
//      return new AuthResponse(email, AuthResponseMessage
//          .USER_ALREADY_EXISTS.getMessage(), null, null, null);
//    }
//    Optional<User> newUser;
    Long householdId;
    try {

      String householdName = newUser.getName() + "'s household";

      double longitude = 0.0;
      double latitude = 0.0;

      householdId = householdRepo.createHousehold(householdName, longitude, latitude);
      System.out.println("2");
    } catch (Exception e) {
      System.out.println("1");
      return new AuthResponse(newUser.getEmail(), AuthResponseMessage
          .HOUSEHOLD_FAILURE.getMessage() + e.getMessage(), null, null, null);
    }
    try {
      newUser.setHouseholdId(householdId);
      userRepo.save(newUser);
      Optional<User> newNewUser = userRepo.findByEmail(newUser.getEmail());
      String token = jwtUtils.generateToken(newNewUser.get().getId(), newUser.getRole());
      jwtUtils.setJwtCookie(token, response);
      System.out.println("4");

      return new AuthResponse(newUser.getEmail(), AuthResponseMessage
          .USER_REGISTERED_SUCCESSFULLY.getMessage(), token,
          jwtUtils.getExpirationDate(token), newUser.getId());
    } catch (Exception e) {
      return new AuthResponse(newUser.getEmail(), AuthResponseMessage
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

