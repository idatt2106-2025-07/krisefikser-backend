package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.request.RegisterAdminRequest;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.UuidUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Map;
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
  void inviteAdmin_shouldSendEmailWithCorrectLink_whenUsernameIsUnique() throws Exception {
    String email = "admin@example.com";
    String fakeToken = "fake.jwt.token";
    String fakeUuid = "abcd1234";
    String expectedUsername = "admin" + fakeUuid;

    InviteAdminRequest request = new InviteAdminRequest();
    request.setEmail(email);

    try (MockedStatic<UuidUtils> mockedUuid = mockStatic(UuidUtils.class)) {
      mockedUuid.when(UuidUtils::generateShortenedUuid).thenReturn(fakeUuid);
      when(userRepository.existAdminByUsername(expectedUsername)).thenReturn(false);
      when(jwtUtils.generateInviteToken(expectedUsername)).thenReturn(fakeToken);

      adminService.inviteAdmin(request);

      verify(emailService).sendTemplateMessage(
          eq(email),
          eq(EmailTemplateType.ADMIN_INVITE),
          eq(Map.of("inviteLink", "https://localhost:5173/invite?token=" + fakeToken))
      );
    }
  }

  @Test
  void inviteAdmin_shouldThrowUsernameGenerationException_whenUsernameIsNotUniqueAfterRetries() {
    String email = "admin@example.com";
    InviteAdminRequest request = new InviteAdminRequest();
    request.setEmail(email);

    try (MockedStatic<UuidUtils> mockedUuid = mockStatic(UuidUtils.class)) {
      mockedUuid.when(UuidUtils::generateShortenedUuid).thenReturn("conflict");

      when(userRepository.existAdminByUsername("adminconflict")).thenReturn(true);

      UsernameGenerationException exception = assertThrows(UsernameGenerationException.class, () -> {
        adminService.inviteAdmin(request);
      });

      assertEquals("Failed to generate a unique username", exception.getMessage());
    }
  }

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
