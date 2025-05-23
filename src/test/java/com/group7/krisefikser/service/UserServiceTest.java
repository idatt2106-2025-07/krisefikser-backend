package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.group7.krisefikser.dto.request.household.HouseholdRequest;
import com.group7.krisefikser.dto.request.user.LoginRequest;
import com.group7.krisefikser.dto.request.user.RegisterRequest;
import com.group7.krisefikser.dto.request.user.ResetPasswordRequest;
import com.group7.krisefikser.dto.response.user.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.user.User;
import com.group7.krisefikser.repository.household.HouseholdRepository;
import com.group7.krisefikser.repository.user.UserRepository;
import com.group7.krisefikser.service.other.EmailService;
import com.group7.krisefikser.service.user.LoginAttemptService;
import com.group7.krisefikser.service.user.UserService;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.PasswordUtil;
import java.util.Date;
import java.util.Optional;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private HttpServletResponse response;

  @Mock
  private LoginAttemptService loginAttemptService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setPassword("hashedPassword");
    testUser.setRole(Role.ROLE_NORMAL);
    testUser.setVerified(true);
    testUser.setHouseholdId(1L);
    ReflectionTestUtils.setField(userService, "frontendUrl", "http://dev.krisefikser.localhost");
    registerRequest = new RegisterRequest(
      "test@example.com",
      "Test User",
      "password123",
      new HouseholdRequest("Test Household", 0.0, 0.0)
    );
    // Setup login request
    loginRequest = new LoginRequest("test@example.com", "password123");
  }

  @Test
  void loadUserByUsername_UserExists_ReturnsUserDetails() {
    // Arrange
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userService.loadUserByUsername("test@example.com");

    // Assert
    assertNotNull(userDetails);
    assertEquals("test@example.com", userDetails.getUsername());
    assertEquals("hashedPassword", userDetails.getPassword());
    verify(userRepository, times(1)).findByEmail("test@example.com");
  }

  @Test
  void loadUserByUsername_UserNotFound_ThrowsException() {
    // Arrange
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername("nonexistent@example.com");
    });
    verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
  }

  @Test
  void registerUser_ExistingUser_ReturnsUserExistsResponse() {
    // Arrange
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));

    // Act
    AuthResponse response = userService.registerUser(registerRequest);

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepository, never()).save(any(User.class));
    verify(householdRepository, never()).createHousehold(anyString(), anyDouble(), anyDouble());
  }

  @Test
  void loginUser_ValidCredentials_ReturnsSuccessResponse() throws JwtMissingPropertyException {
    // Arrange
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(jwtUtils.generateToken(anyLong(), any(Role.class))).thenReturn("auth-token");
    when(jwtUtils.getExpirationDate(anyString())).thenReturn(new Date());
    doNothing().when(jwtUtils).setJwtCookie(anyString(), any(HttpServletResponse.class));

    try (MockedStatic<PasswordUtil> passwordUtilMockedStatic = mockStatic(PasswordUtil.class)) {
      passwordUtilMockedStatic.when(() -> PasswordUtil.verifyPassword(anyString(), anyString())).thenReturn(true);

      // Act
      AuthResponse response = userService.loginUser(loginRequest, this.response);

      // Assert
      assertNotNull(response);
      assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
      assertNotNull(response.getExpiryDate());
      assertEquals(Role.ROLE_NORMAL, response.getRole());

      verify(jwtUtils, times(1)).generateToken(anyLong(), any(Role.class));
      verify(jwtUtils, times(1)).setJwtCookie(anyString(), any(HttpServletResponse.class));
    }
  }

  @Test
  void loginUser_UserNotFound_ReturnsErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    // Act
    AuthResponse response = userService.loginUser(loginRequest, this.response);

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
  }

  @Test
  void loginUser_UserNotVerified_ReturnsErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    testUser.setVerified(false);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    // Act
    AuthResponse response = userService.loginUser(loginRequest, this.response);

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.USER_NOT_VERIFIED.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
  }

  @Test
  void loginUser_InvalidPassword_ReturnsErrorResponse() {
    // Arrange
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    try (MockedStatic<PasswordUtil> passwordUtilMockedStatic = mockStatic(PasswordUtil.class)) {
      passwordUtilMockedStatic.when(() -> PasswordUtil.verifyPassword(anyString(), anyString())).thenReturn(false);

      // Act
      AuthResponse response = userService.loginUser(loginRequest, this.response);

      // Assert
      assertNotNull(response);
      assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
      assertNull(response.getExpiryDate());
      assertNull(response.getRole());

      verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
    } catch (JwtMissingPropertyException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void loginUser_JwtGenerationFails_ReturnsErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(jwtUtils.generateToken(anyLong(), any(Role.class))).thenThrow(new RuntimeException("JWT error"));

    try (MockedStatic<PasswordUtil> passwordUtilMockedStatic = mockStatic(PasswordUtil.class)) {
      passwordUtilMockedStatic.when(() -> PasswordUtil.verifyPassword(anyString(), anyString())).thenReturn(true);

      // Act
      AuthResponse response = userService.loginUser(loginRequest, this.response);

      // Assert
      assertNotNull(response);
      assertTrue(response.getMessage().contains(AuthResponseMessage.USER_LOGIN_ERROR.getMessage()));
      assertNull(response.getExpiryDate());
      assertNull(response.getRole());
    }
  }

  @Test
  void verifyEmail_ValidToken_ReturnsSuccessResponse() throws JwtMissingPropertyException {
    // Arrange
    when(jwtUtils.validateVerificationTokenAndGetEmail("valid-token")).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(userRepository.setVerified(any(User.class))).thenReturn(Optional.empty());

    // Act
    AuthResponse response = userService.verifyEmail("valid-token");

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.USER_VERIFIED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepository, times(1)).setVerified(any(User.class));
  }

  @Test
  void verifyEmail_UserNotFound_ReturnsErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    when(jwtUtils.validateVerificationTokenAndGetEmail("valid-token")).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    // Act
    AuthResponse response = userService.verifyEmail("valid-token");

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepository, never()).setVerified(any(User.class));
  }

  @Test
  void verifyEmail_InvalidToken_ReturnsErrorResponse() throws JwtMissingPropertyException {
    // Arrange
    when(jwtUtils.validateVerificationTokenAndGetEmail("invalid-token"))
        .thenThrow(new JwtMissingPropertyException("Invalid token"));

    // Act
    AuthResponse response = userService.verifyEmail("invalid-token");

    // Assert
    assertNotNull(response);
    assertEquals(AuthResponseMessage.INVALID_TOKEN.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepository, never()).findByEmail(anyString());
    verify(userRepository, never()).setVerified(any(User.class));
  }

  @Test
  void resetPassword_ShouldResetPassword_WhenTokenValidAndUserExists() throws JwtMissingPropertyException {
    String token = "validToken";
    String email = "test@user.com";
    String newPassword = "NewStrongPassword1!";
    ResetPasswordRequest request = new ResetPasswordRequest(token, email, newPassword);

    when(jwtUtils.validateResetPasswordTokenAndGetEmail(token)).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    try (MockedStatic<PasswordUtil> mockedPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
      mockedPasswordUtil
          .when(() -> PasswordUtil.hashPassword(newPassword))
          .thenReturn("newHashedPassword");

      AuthResponse response = userService.resetPassword(request);

      assertEquals(AuthResponseMessage.PASSWORD_RESET_SUCCESS.getMessage(), response.getMessage());
      verify(userRepository).updatePasswordByEmail("test@example.com", "newHashedPassword");
    }
  }

  @Test
  void resetPassword_ShouldReturnUserNotFound_WhenEmailInvalid() throws JwtMissingPropertyException {
    ResetPasswordRequest request = new ResetPasswordRequest("token", "email@tester.com", "password");

    when(jwtUtils.validateResetPasswordTokenAndGetEmail("token")).thenReturn("missing@example.com");
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    AuthResponse response = userService.resetPassword(request);

    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
  }

  @Test
  void resetPassword_ShouldReturnInvalidToken_WhenJwtExceptionThrown() throws JwtMissingPropertyException {
    ResetPasswordRequest request = new ResetPasswordRequest("badToken", "email@tester.com", "password");

    when(jwtUtils.validateResetPasswordTokenAndGetEmail("badToken"))
        .thenThrow(new JwtMissingPropertyException("Missing"));

    AuthResponse response = userService.resetPassword(request);

    assertEquals(AuthResponseMessage.INVALID_TOKEN.getMessage(), response.getMessage());
  }

  @Test
  void resetPassword_ShouldRejectWeakPassword_WhenUserIsAdmin() throws JwtMissingPropertyException {
    testUser.setRole(Role.ROLE_ADMIN);
    ResetPasswordRequest request = new ResetPasswordRequest("token", "email@test.no", "weak");

    when(jwtUtils.validateResetPasswordTokenAndGetEmail("token")).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    try (MockedStatic<PasswordUtil> mockedPasswordUtil = Mockito.mockStatic(PasswordUtil.class)) {
      mockedPasswordUtil.when(() -> PasswordUtil.isStrongPassword("weak")).thenReturn(false);

      AuthResponse response = userService.resetPassword(request);

      assertEquals(AuthResponseMessage.PASSWORD_TOO_WEAK.getMessage(), response.getMessage());
    }
  }

  @Test
  void sendResetPasswordLink_ShouldSendEmail_WhenUserExists() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(jwtUtils.generateResetPasswordToken("test@example.com")).thenReturn("resetToken");

    userService.sendResetPasswordLink("test@example.com");

    String expectedLink = "http://dev.krisefikser.localhost/reset-password?token=resetToken";
    verify(emailService).sendTemplateMessage(eq("test@example.com"), eq(EmailTemplateType.PASSWORD_RESET),
        argThat(map -> expectedLink.equals(map.get("resetLink"))));
  }

  @Test
  void sendResetPasswordLink_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> userService.sendResetPasswordLink("unknown@example.com"));

    verify(emailService, never()).sendTemplateMessage(any(), any(), any());
  }
}