package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.Role;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  void registerUser_whenUserAlreadyExists_returnsUserAlreadyExistsResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("existing@example.com", "Existing User", "password", "12345678", null);
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

    // Act
    AuthResponse response = userService.registerUser(request);

    // Assert
    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getToken());
    assertNull(response.getExpiryDate());
    assertNull(response.getId());

    verify(userRepo, never()).save(any(User.class));
    verify(jwtUtils, never()).generateToken(any(), any());
  }

  @Test
  void registerUser_whenNewUserSavedSuccessfully_returnsSuccessResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("new@example.com", "New User", "password", "98765432", 69L);
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setRole(Role.ROLE_USER);

    when(userRepo.save(any(User.class))).thenReturn(Optional.of(savedUser));

    String fakeToken = "fake.jwt.token";
    Date expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000); // 1 time frem i tid
    when(jwtUtils.generateToken(savedUser.getId(), savedUser.getRole())).thenReturn(fakeToken);
    when(jwtUtils.getExpirationDate(fakeToken)).thenReturn(expirationDate);

    // Act
    AuthResponse response = userService.registerUser(request);

    // Assert
    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals(fakeToken, response.getToken());
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(savedUser.getId(), response.getId());

    verify(userRepo).save(any(User.class));
    verify(jwtUtils).generateToken(savedUser.getId(), savedUser.getRole());
  }

  @Test
  void registerUser_whenSaveFails_returnsSavingUserErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("fail@example.com", "Fail User", "password", "11223344", 96L);
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Database is down"));

    // Act
    AuthResponse response = userService.registerUser(request);

    // Assert
    assertTrue(response.getMessage().contains(AuthResponseMessage.SAVING_USER_ERROR.getMessage()));
    assertTrue(response.getMessage().contains("Database is down"));
    assertNull(response.getToken());
    assertNull(response.getExpiryDate());
    assertNull(response.getId());

    verify(userRepo).save(any(User.class));
    verify(jwtUtils, never()).generateToken(any(), any());
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

  @Test
  void userExists_whenUserExists_returnsTrue() {
    // Arrange
    Long userId = 1L;
    when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));

    // Act
    boolean exists = userService.userExists(userId);

    // Assert
    assertTrue(exists);
    verify(userRepo).findById(userId);
  }

  @Test
  void userExists_whenUserDoesNotExist_returnsFalse() {
    // Arrange
    Long userId = 2L;
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    // Act
    boolean exists = userService.userExists(userId);

    // Assert
    assertFalse(exists);
    verify(userRepo).findById(userId);
  }

  @Test
  void validateUserIdMatchesToken_whenIdsMatch_returnsTrue() throws JwtMissingPropertyException {
    // Arrange
    String token = "valid.token.here";
    Long userId = 123L;
    when(jwtUtils.validateTokenAndGetUserId(token)).thenReturn(String.valueOf(userId));

    // Act
    boolean matches = userService.validateUserIdMatchesToken(token, userId);

    // Assert
    assertTrue(matches);
    verify(jwtUtils).validateTokenAndGetUserId(token);
  }

  @Test
  void validateUserIdMatchesToken_whenIdsDoNotMatch_returnsFalse() throws JwtMissingPropertyException {
    // Arrange
    String token = "valid.token.here";
    Long userId = 123L;
    when(jwtUtils.validateTokenAndGetUserId(token)).thenReturn(String.valueOf(999L)); // feil ID

    // Act
    boolean matches = userService.validateUserIdMatchesToken(token, userId);

    // Assert
    assertFalse(matches);
    verify(jwtUtils).validateTokenAndGetUserId(token);
  }

  @Test
  void validateUserIdMatchesToken_whenTokenIsInvalid_throwsException() throws JwtMissingPropertyException {
    // Arrange
    String token = "invalid.token.here";
    Long userId = 123L;
    when(jwtUtils.validateTokenAndGetUserId(token)).thenThrow(new JwtMissingPropertyException("Missing user ID"));

    // Act & Assert
    assertThrows(JwtMissingPropertyException.class, () -> {
      userService.validateUserIdMatchesToken(token, userId);
    });

    verify(jwtUtils).validateTokenAndGetUserId(token);
  }

  @Test
  void validateAdmin_whenRoleIsAdmin_returnsTrue() throws JwtMissingPropertyException {
    // Arrange
    String token = "admin.token.here";
    when(jwtUtils.validateTokenAndGetRole(token)).thenReturn(Role.ROLE_ADMIN.toString());

    // Act
    boolean isAdmin = userService.validateAdmin(token);

    // Assert
    assertTrue(isAdmin);
    verify(jwtUtils).validateTokenAndGetRole(token);
  }

  @Test
  void validateAdmin_whenRoleIsNotAdmin_returnsFalse() throws JwtMissingPropertyException {
    // Arrange
    String token = "user.token.here";
    when(jwtUtils.validateTokenAndGetRole(token)).thenReturn(Role.ROLE_USER.toString());

    // Act
    boolean isAdmin = userService.validateAdmin(token);

    // Assert
    assertFalse(isAdmin);
    verify(jwtUtils).validateTokenAndGetRole(token);
  }

  @Test
  void validateAdmin_whenTokenIsInvalid_throwsException() throws JwtMissingPropertyException {
    // Arrange
    String token = "invalid.token.here";
    when(jwtUtils.validateTokenAndGetRole(token)).thenThrow(new JwtMissingPropertyException("Missing role"));

    // Act & Assert
    assertThrows(JwtMissingPropertyException.class, () -> {
      userService.validateAdmin(token);
    });

    verify(jwtUtils).validateTokenAndGetRole(token);
  }

  @Test
  void refreshToken_whenValidToken_returnsNewAuthResponse() throws JwtMissingPropertyException {
    // Arrange
    String oldToken = "old.token.here";
    Long userId = 1L;
    String role = Role.ROLE_USER.toString();
    String newToken = "new.token.here";
    Date expirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // f.eks. 1 time fram

    when(jwtUtils.validateTokenAndGetUserId(oldToken)).thenReturn(String.valueOf(userId));
    when(jwtUtils.validateTokenAndGetRole(oldToken)).thenReturn(role);
    when(jwtUtils.generateToken(userId, Role.valueOf(role))).thenReturn(newToken);
    when(jwtUtils.getExpirationDate(newToken)).thenReturn(expirationDate);

    // Act
    AuthResponse response = userService.refreshToken(oldToken);

    // Assert
    assertNotNull(response);
    assertEquals(String.valueOf(userId), response.getEmail());
    assertEquals(AuthResponseMessage.TOKEN_REFRESH_ERROR.getMessage(), response.getMessage());
    assertEquals(newToken, response.getToken());
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(userId, response.getId());

    verify(jwtUtils).validateTokenAndGetUserId(oldToken);
    verify(jwtUtils).validateTokenAndGetRole(oldToken);
    verify(jwtUtils).generateToken(userId, Role.valueOf(role));
    verify(jwtUtils).getExpirationDate(newToken);
  }

  @Test
  void refreshToken_whenInvalidToken_throwsException() throws JwtMissingPropertyException {
    // Arrange
    String invalidToken = "invalid.token.here";

    when(jwtUtils.validateTokenAndGetUserId(invalidToken))
        .thenThrow(new JwtMissingPropertyException("Invalid token"));

    // Act & Assert
    assertThrows(JwtMissingPropertyException.class, () -> {
      userService.refreshToken(invalidToken);
    });

    verify(jwtUtils).validateTokenAndGetUserId(invalidToken);
  }
}
