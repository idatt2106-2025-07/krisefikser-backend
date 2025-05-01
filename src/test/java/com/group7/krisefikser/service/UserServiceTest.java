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
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

  @Mock
  private UserRepository userRepo;

  @Mock
  private HouseholdRepository householdRepo;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private EmailService emailService;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private UserService userService;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    registerRequest = new RegisterRequest("Alice", "alice@example.com", "securePassword123");
    loginRequest = new LoginRequest("alice@example.com", "securePassword123");
  }

  @Test
  void registerUser_newUser_success() throws JwtMissingPropertyException {
    User user = new User();
    user.setId(1L);
    user.setEmail(registerRequest.getEmail());
    user.setName(registerRequest.getName());
    user.setRole(Role.ROLE_NORMAL);
    user.setPassword(PasswordUtil.hashPassword(registerRequest.getPassword())); // riktig hashing

    // Første kall: bruker finnes ikke
    when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty())  // første kall
        .thenReturn(Optional.of(user)); // andre kall

    when(householdRepo.existsByName(anyString())).thenReturn(false);
    when(householdRepo.createHousehold(anyString(), anyDouble(), anyDouble())).thenReturn(1L);
    when(userRepo.save(any(User.class))).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(eq(1L), eq(Role.ROLE_NORMAL))).thenReturn("jwt-token");
    when(jwtUtils.getExpirationDate(anyString())).thenReturn(new Date());

    AuthResponse authResponse = userService.registerUser(registerRequest, response);

    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), authResponse.getMessage());
    assertNotNull(authResponse.getExpiryDate());
    assertEquals(Role.ROLE_NORMAL, authResponse.getRole());
  }

  @Test
  void registerUser_existingUser_returnsAlreadyExists() {
    User existingUser = new User();
    existingUser.setEmail(registerRequest.getEmail());

    when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(existingUser));

    AuthResponse result = userService.registerUser(registerRequest, this.response);

    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), result.getMessage());
    assertNull(result.getExpiryDate());
    assertNull(result.getRole());
  }

  @Test
  void loginUser_validCredentials_returnsSuccess() throws JwtMissingPropertyException {
    String hashedPassword = PasswordUtil.hashPassword(loginRequest.getPassword());

    User user = new User();
    user.setId(1L);
    user.setEmail(loginRequest.getEmail());
    user.setPassword(hashedPassword);
    user.setRole(Role.ROLE_NORMAL);

    when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(eq(1L), eq(Role.ROLE_NORMAL))).thenReturn("jwt-token");
    when(jwtUtils.getExpirationDate("jwt-token")).thenReturn(new Date());

    AuthResponse response = userService.loginUser(loginRequest, this.response);

    assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
    assertNotNull(response.getExpiryDate());
    assertEquals(Role.ROLE_NORMAL, response.getRole());
  }

  @Test
  void loginUser_wrongPassword_returnsInvalidCredentials() {
    String wrongPassword = PasswordUtil.hashPassword("wrongPassword");

    User user = new User();
    user.setEmail(loginRequest.getEmail());
    user.setPassword(wrongPassword);

    when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));

    AuthResponse response = userService.loginUser(loginRequest, this.response);

    assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
  }

  @Test
  void loginUser_userNotFound_returnsNotFound() {
    when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

    AuthResponse response = userService.loginUser(loginRequest, this.response);

    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
  }
}