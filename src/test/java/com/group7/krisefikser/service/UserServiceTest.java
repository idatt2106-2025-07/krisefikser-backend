package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import com.group7.krisefikser.dto.request.LoginRequest;
import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.dto.response.AuthResponse;
import com.group7.krisefikser.enums.AuthResponseMessage;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private EmailService emailService;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    // Setup test user
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setPassword("hashedPassword");
    testUser.setRole(Role.ROLE_NORMAL);
    testUser.setVerified(true);
    testUser.setHouseholdId(1L);

    // Setup register request
    registerRequest = new RegisterRequest("test@example.com", "Test User", "password123");
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
  void registerUser_NewUser_ReturnsSuccessResponse() throws JwtMissingPropertyException {
    // Arrange
    when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty()) // First call (user doesn't exist check)
        .thenReturn(Optional.of(testUser)); // Second call (after saving)

    when(householdRepository.existsByName(anyString())).thenReturn(false);
    when(householdRepository.createHousehold(anyString(), anyDouble(), anyDouble())).thenReturn(1L);

    testUser.setVerified(false); // The newly created user should not be verified

    when(userRepository.save(any(User.class))).thenReturn(Optional.ofNullable(testUser));

    when(jwtUtils.generateVerificationToken(anyString())).thenReturn("verification-token");
    doNothing().when(emailService).sendTemplateMessage(anyString(), any(EmailTemplateType.class), anyMap());

    try (MockedStatic<PasswordUtil> passwordUtilMockedStatic = mockStatic(PasswordUtil.class)) {
      passwordUtilMockedStatic.when(() -> PasswordUtil.hashPassword(anyString())).thenReturn("hashedPassword");

      // Act
      AuthResponse response = userService.registerUser(registerRequest);

      // Assert
      assertNotNull(response);
      assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
      assertNull(response.getExpiryDate());
      assertEquals(Role.ROLE_NORMAL, response.getRole());

      verify(userRepository, times(1)).save(any(User.class));
      verify(householdRepository, times(1)).createHousehold(anyString(), anyDouble(), anyDouble());
      verify(emailService, times(1)).sendTemplateMessage(anyString(), eq(EmailTemplateType.VERIFY_EMAIL), anyMap());
    }
  }

  @Test
  void registerUser_ExistingUser_ReturnsUserExistsResponse() {
    // Arrange
    // Use any() matcher to handle any parameter passed to findByEmail
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
  void registerUser_HouseholdCreationFails_ReturnsErrorResponse() {
    // Arrange
    // Use lenient() to avoid strict stubbing issues
    lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(householdRepository.existsByName(anyString())).thenReturn(false);
    when(householdRepository.createHousehold(anyString(), anyDouble(), anyDouble()))
        .thenThrow(new RuntimeException("Database error"));

    // Act
    AuthResponse response = userService.registerUser(registerRequest);

    // Assert
    assertNotNull(response);
    assertTrue(response.getMessage().contains(AuthResponseMessage.HOUSEHOLD_FAILURE.getMessage()));
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    verify(userRepository, never()).save(any(User.class));
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
    // For non-void methods, use when().thenReturn() pattern
    when(userRepository.setVerified(any(User.class))).thenReturn(Optional.empty()); // Assuming it returns rows affected

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
}