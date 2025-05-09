package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.user.RegisterAdminRequest;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.repository.user.UserRepository;
import com.group7.krisefikser.service.household.HouseholdService;
import com.group7.krisefikser.service.other.EmailService;
import com.group7.krisefikser.service.user.AdminService;
import com.group7.krisefikser.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @InjectMocks
  private AdminService adminService;

  @Mock
  private EmailService emailService;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private UserRepository userRepository;

  @Mock
  private HouseholdService householdService;



  @Test
  void registerAdmin_shouldSaveAdmin_whenValidTokenAndUniqueUsername() throws Exception {
    String token = "valid.jwt.token";
    String username = "admin123";
    String email = "admin@example.com";
    String password = "securePassword";
    long householdId = 42L;

    RegisterAdminRequest request = new RegisterAdminRequest();
    request.setToken(token);
    request.setEmail(email);
    request.setPassword(password);

    when(jwtUtils.validateInviteAdminTokenAndGetUsername(token)).thenReturn(username);
    when(userRepository.existAdminByUsername(username)).thenReturn(false);
    when(householdService.createHouseholdForUser(username)).thenReturn(householdId);

    adminService.registerAdmin(request);

    verify(userRepository).save(argThat(user ->
        user.getName().equals(username) &&
            user.getEmail().equals(email) &&
            user.getRole().toString().equals("ROLE_ADMIN") &&
            user.getHouseholdId().equals(householdId)
    ));
  }

  @Test
  void registerAdmin_shouldThrowException_whenUsernameAlreadyExists() throws Exception {
    String token = "valid.jwt.token";
    String username = "admin123";
    RegisterAdminRequest request = new RegisterAdminRequest();
    request.setToken(token);
    request.setEmail("admin@example.com");
    request.setPassword("securePassword");

    when(jwtUtils.validateInviteAdminTokenAndGetUsername(request.getToken())).thenReturn(username);
    when(userRepository.existAdminByUsername(username)).thenReturn(true);

    UsernameGenerationException exception = assertThrows(UsernameGenerationException.class, () ->
        adminService.registerAdmin(request));

    assertEquals("Username already taken", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void verifyTwoFactor_shouldSetJwtCookie_whenTokenIsValid() throws Exception {
    String twoFactorToken = "2fa.token";
    String userId = "99";
    String jwt = "jwt.token";

    HttpServletResponse response = mock(HttpServletResponse.class);

    when(jwtUtils.validate2faTokenAndGetUserId(twoFactorToken)).thenReturn(userId);
    when(jwtUtils.generateToken(Long.parseLong(userId), Role.ROLE_ADMIN)).thenReturn(jwt);

    adminService.verifyTwoFactor(twoFactorToken, response);

    verify(jwtUtils).setJwtCookie(jwt, response);
  }
}
