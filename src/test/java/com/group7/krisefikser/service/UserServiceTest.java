package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UserServiceTest {
  @Mock
  private UserRepository userRepo;

  @Mock
  private JwtUtils jwtUtils;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void loginUser_userNotFound_returnsUserNotFoundResponse() throws JwtMissingPropertyException {
    LoginRequest request = new LoginRequest("test@example.com", "password123");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    AuthResponse response = userService.loginUser(request);

    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getToken());
    assertNull(response.getExpiryDate());
    assertNull(response.getId());
  }

  @Test
  void loginUser_invalidPassword_returnsInvalidCredentialsResponse() throws JwtMissingPropertyException {
    LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(PasswordUtil.hashPassword("correctPassword")); // Antar du har en hashPassword-metode.

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    // Her må vi mocke PasswordUtil hvis den ikke er statisk.

    // Hvis PasswordUtil er statisk må vi bruke PowerMockito, men vi antar nå at den fungerer direkte.

    AuthResponse response = userService.loginUser(request);

    assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
    assertNull(response.getToken());
    assertNull(response.getExpiryDate());
    assertNull(response.getId());
  }

  @Test
  void loginUser_validCredentials_returnsSuccessResponse() throws JwtMissingPropertyException {
    LoginRequest request = new LoginRequest("test@example.com", "correctPassword");
    User user = new User();
    user.setId(1L);
    user.setEmail(request.getEmail());
    user.setPassword(PasswordUtil.hashPassword("correctPassword")); // Igjen, antar hash.

    String generatedToken = "fake.jwt.token";
    Date expirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 time frem

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(user.getId(), user.getRole())).thenReturn(generatedToken);
    when(jwtUtils.getExpirationDate(generatedToken)).thenReturn(expirationDate);

    AuthResponse response = userService.loginUser(request);

    assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals(generatedToken, response.getToken());
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(user.getId(), response.getId());
  }
}
