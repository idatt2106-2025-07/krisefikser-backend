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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

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

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void registerUser_successfulRegistration() throws JwtMissingPropertyException {
    RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
    User user = UserMapper.INSTANCE.registerRequestToUser(request);
    user.setId(1L);
    user.setRole(Role.ROLE_NORMAL);

    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(householdRepo.existsByName(anyString())).thenReturn(false);
    when(householdRepo.createHousehold(anyString(), anyDouble(), anyDouble())).thenReturn(123L);
    when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(user.getId(), user.getRole())).thenReturn("token");
    when(jwtUtils.getExpirationDate("token")).thenReturn(new Date());

    AuthResponse response = userService.registerUser(request, httpServletResponse);

    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertNotNull(response.getExpiryDate());
    assertEquals(Role.ROLE_NORMAL, response.getRole());
    verify(emailService).sendTemplateMessage(eq("test@example.com"), any(), any());
  }

  @Test
  void registerUser_userAlreadyExists() {
    RegisterRequest request = new RegisterRequest("Test", "exists@example.com", "password");
    when(userRepo.findByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

    AuthResponse response = userService.registerUser(request, httpServletResponse);

    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
  }

  @Test
  void loginUser_successfulLogin() throws JwtMissingPropertyException {
    String rawPassword = "password123";
    String hashedPassword = PasswordUtil.hashPassword(rawPassword);
    User user = new User();
    user.setId(1L);
    user.setEmail("login@example.com");
    user.setPassword(hashedPassword);
    user.setRole(Role.ROLE_NORMAL);

    LoginRequest loginRequest = new LoginRequest("login@example.com", rawPassword);
    when(userRepo.findByEmail("login@example.com")).thenReturn(Optional.of(user));
    when(jwtUtils.generateToken(1L, Role.ROLE_NORMAL)).thenReturn("jwt-token");
    when(jwtUtils.getExpirationDate("jwt-token")).thenReturn(new Date());

    AuthResponse response = userService.loginUser(loginRequest, httpServletResponse);

    assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals(Role.ROLE_NORMAL, response.getRole());
    assertNotNull(response.getExpiryDate());
  }

  @Test
  void loginUser_userNotFound() {
    LoginRequest request = new LoginRequest("notfound@example.com", "password");
    when(userRepo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    AuthResponse response = userService.loginUser(request, httpServletResponse);

    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
  }

  @Test
  void loginUser_invalidPassword() {
    String hashedPassword = PasswordUtil.hashPassword("correctPassword");
    User user = new User();
    user.setEmail("invalidpass@example.com");
    user.setPassword(hashedPassword);

    LoginRequest request = new LoginRequest("invalidpass@example.com", "wrongPassword");
    when(userRepo.findByEmail("invalidpass@example.com")).thenReturn(Optional.of(user));

    AuthResponse response = userService.loginUser(request, httpServletResponse);

    assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
    assertNull(response.getExpiryDate());
    assertNull(response.getRole());
  }
}