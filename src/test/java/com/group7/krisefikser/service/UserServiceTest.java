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
import jakarta.servlet.http.HttpServletResponse;
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

  @Mock
  private HouseholdRepository householdRepo;

  @Mock
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerUser_whenUserAlreadyExists_returnsUserAlreadyExistsResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("test", "test@example.com", "password");
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

    // Act
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    AuthResponse response = userService.registerUser(request, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepo, never()).save(any(User.class));
    verify(jwtUtils, never()).generateToken(any(), any());
  }

  @Test
  void registerUser_whenNewUserSavedSuccessfully_returnsSuccessResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password");

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setEmail(request.getEmail());
    savedUser.setRole(Role.ROLE_NORMAL);

    // Første kall: sjekk om bruker finnes (skal ikke finnes)
    // Andre kall: etter lagring, hent bruker for token-generering
    when(userRepo.findByEmail(request.getEmail()))
        .thenReturn(Optional.empty()) // første kall: bruker finnes ikke
        .thenReturn(Optional.of(savedUser)); // andre kall: bruker finnes nå

    when(userRepo.save(any(User.class))).thenAnswer(invocation -> null); // save returnerer void

    String fakeToken = "fake.jwt.token";
    Date expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000);
    when(jwtUtils.generateToken(savedUser.getId(), savedUser.getRole())).thenReturn(fakeToken);
    when(jwtUtils.getExpirationDate(fakeToken)).thenReturn(expirationDate);

    // Act
    AuthResponse response = userService.registerUser(request, mock(HttpServletResponse.class));

    // Assert
    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(savedUser.getRole(), response.getRole());

    verify(userRepo).save(any(User.class));
    verify(jwtUtils, times(2)).generateToken(savedUser.getId(), savedUser.getRole());
  }

  @Test
  void registerUser_whenSaveFails_returnsSavingUserErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("Fail User", "fail@example.com", "password");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Database is down"));

    // Act
    AuthResponse response = userService.registerUser(request, mock(HttpServletResponse.class));

    // Assert
    assertTrue(response.getMessage().contains(AuthResponseMessage.SAVING_USER_ERROR.getMessage()));
    assertTrue(response.getMessage().contains("Database is down"));
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepo).save(any(User.class));
    verify(jwtUtils, never()).generateToken(any(), any());
  }

  @Test
  void loginUser_userNotFound_returnsUserNotFoundResponse() throws JwtMissingPropertyException {
    LoginRequest request = new LoginRequest("test@example.com", "password123");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    AuthResponse response = userService.loginUser(request);

    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
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
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
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
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(user.getRole(), response.getRole());
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
    when(jwtUtils.validateTokenAndGetRole(token)).thenReturn(Role.ROLE_NORMAL.toString());

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
    String role = Role.ROLE_NORMAL.toString();
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
    assertEquals(AuthResponseMessage.TOKEN_REFRESH_SUCCESS.getMessage(), response.getMessage());
    assertEquals(expirationDate, response.getExpiryDate());
    assertEquals(role, response.getRole().toString());

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
