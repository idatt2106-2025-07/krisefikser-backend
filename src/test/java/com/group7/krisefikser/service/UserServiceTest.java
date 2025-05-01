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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepo;

  @Mock
  private HouseholdRepository householdRepo;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private EmailService emailService;

  @Mock
  private HttpServletResponse httpServletResponse;

  @Mock
  private UserMapper userMapper;

  @Captor
  private ArgumentCaptor<User> userCaptor;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerUser_successfulRegistration() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");

    User newUser = new User();
    newUser.setName("Test User");
    newUser.setEmail("test@example.com");
    newUser.setPassword(PasswordUtil.hashPassword("password123"));
    newUser.setRole(Role.ROLE_NORMAL);

    User savedUser = new User();
    savedUser.setId(1L);
    savedUser.setName("Test User");
    savedUser.setEmail("test@example.com");
    savedUser.setPassword(PasswordUtil.hashPassword("password123"));
    savedUser.setRole(Role.ROLE_NORMAL);
    savedUser.setHouseholdId(123L);

    // Mock the user repository behavior
    when(userRepo.findByEmail("test@example.com"))
        .thenReturn(Optional.empty()) // First call during check
        .thenReturn(Optional.of(savedUser)); // Second call after save

    when(userRepo.save(any(User.class))).thenReturn(Optional.of(savedUser));

    // Mock household repository
    when(householdRepo.existsByName(anyString())).thenReturn(false);
    when(householdRepo.createHousehold(anyString(), anyDouble(), anyDouble())).thenReturn(123L);

    // Mock JWT utils
    String verificationToken = "verification-token";
    String authToken = "auth-token";
    Date expiryDate = new Date();

    when(jwtUtils.generateToken(eq(1L), eq(Role.ROLE_NORMAL)))
        .thenReturn(verificationToken)
        .thenReturn(authToken);
    when(jwtUtils.getExpirationDate(authToken)).thenReturn(expiryDate);
    doNothing().when(jwtUtils).setJwtCookie(eq(authToken), eq(httpServletResponse));

    // Mock email service
    doNothing().when(emailService).sendTemplateMessage(
        eq("test@example.com"),
        eq(EmailTemplateType.VERIFY_EMAIL),
        any(Map.class)
    );

    // Act
    AuthResponse response = userService.registerUser(request, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertNotNull(response.getExpiryDate());
    assertEquals(Role.ROLE_NORMAL, response.getRole());

    // Verify calls
    verify(userRepo).save(any(User.class));
    verify(householdRepo).createHousehold(anyString(), eq(0.0), eq(0.0));
    verify(jwtUtils).setJwtCookie(eq(authToken), eq(httpServletResponse));
    verify(emailService).sendTemplateMessage(
        eq("test@example.com"),
        eq(EmailTemplateType.VERIFY_EMAIL),
        any(Map.class)
    );
  }

  @Test
  void registerUser_userAlreadyExists() throws JwtMissingPropertyException {
    // Arrange
    RegisterRequest request = new RegisterRequest("Test", "exists@example.com", "password");
    User existingUser = new User();
    existingUser.setEmail("exists@example.com");

    when(userRepo.findByEmail("exists@example.com")).thenReturn(Optional.of(existingUser));

    // Act
    AuthResponse response = userService.registerUser(request, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    // Verify no further actions were taken
    verify(userRepo, never()).save(any(User.class));
    verify(householdRepo, never()).createHousehold(anyString(), anyDouble(), anyDouble());
    verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
    verify(jwtUtils, never()).setJwtCookie(anyString(), any(HttpServletResponse.class));
  }

  @Test
  void loginUser_successfulLogin() throws JwtMissingPropertyException {
    // Arrange
    String rawPassword = "password123";
    String hashedPassword = PasswordUtil.hashPassword(rawPassword);

    User user = new User();
    user.setId(1L);
    user.setEmail("login@example.com");
    user.setPassword(hashedPassword);
    user.setRole(Role.ROLE_NORMAL);

    LoginRequest loginRequest = new LoginRequest("login@example.com", rawPassword);
    Date expiryDate = new Date();
    String token = "jwt-token";

    when(userRepo.findByEmail("login@example.com")).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(1L, Role.ROLE_NORMAL)).thenReturn(token);
    when(jwtUtils.getExpirationDate(token)).thenReturn(expiryDate);
    doNothing().when(jwtUtils).setJwtCookie(eq(token), eq(httpServletResponse));

    // Act
    AuthResponse response = userService.loginUser(loginRequest, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals(Role.ROLE_NORMAL, response.getRole());
    assertEquals(expiryDate, response.getExpiryDate());

    // Verify JWT token was set
    verify(jwtUtils).setJwtCookie(token, httpServletResponse);
  }

  @Test
  void loginUser_userNotFound() throws JwtMissingPropertyException {
    // Arrange
    LoginRequest request = new LoginRequest("notfound@example.com", "password");
    when(userRepo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    // Act
    AuthResponse response = userService.loginUser(request, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    // Verify JWT operations were not called
    verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
    verify(jwtUtils, never()).setJwtCookie(anyString(), any(HttpServletResponse.class));
  }

  @Test
  void loginUser_invalidPassword() throws JwtMissingPropertyException {
    // Arrange
    String hashedPassword = PasswordUtil.hashPassword("correctPassword");
    User user = new User();
    user.setId(1L);
    user.setEmail("invalidpass@example.com");
    user.setPassword(hashedPassword);

    LoginRequest request = new LoginRequest("invalidpass@example.com", "wrongPassword");
    when(userRepo.findByEmail("invalidpass@example.com")).thenReturn(Optional.of(user));

    // Act
    AuthResponse response = userService.loginUser(request, httpServletResponse);

    // Assert
    assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());

    // Verify JWT operations were not called
    verify(jwtUtils, never()).generateToken(anyLong(), any(Role.class));
    verify(jwtUtils, never()).setJwtCookie(anyString(), any(HttpServletResponse.class));
  }
}