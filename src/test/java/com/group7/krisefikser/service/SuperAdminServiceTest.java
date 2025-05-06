package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.InviteAdminRequest;
import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.UsernameGenerationException;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import com.group7.krisefikser.utils.UuidUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SuperAdminServiceTest {

  @InjectMocks
  private SuperAdminService superAdminService;

  @Mock
  private EmailService emailService;

  @Mock
  private UserService userService;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private UserRepository userRepository;

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

      superAdminService.inviteAdmin(request);

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
        superAdminService.inviteAdmin(request);
      });

      assertEquals("Failed to generate a unique username", exception.getMessage());
    }
  }

  @Test
  void getAllAdmins_ShouldReturnListOfAdmins_WhenAdminsExist() {
    User admin = new User();
    admin.setId(1L);
    admin.setEmail("admin@test.com");
    admin.setRole(Role.ROLE_ADMIN);

    when(userRepository.findByRole(Role.ROLE_ADMIN)).thenReturn(List.of(admin));

    List<SuperAdminResponse> result = superAdminService.getAllAdmins();

    assertEquals(1, result.size());
    assertEquals("admin@test.com", result.get(0).getEmail());
  }

  @Test
  void getAllAdmins_ShouldThrowException_WhenNoAdminsFound() {
    when(userRepository.findByRole(Role.ROLE_ADMIN)).thenReturn(Collections.emptyList());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.getAllAdmins());

    assertTrue(exception.getMessage().contains("No admins found"));
  }

  @Test
  void deleteAdmin_ShouldDeleteAdmin_WhenUserIsAdmin() {
    User admin = new User();
    admin.setId(1L);
    admin.setRole(Role.ROLE_ADMIN);

    when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

    superAdminService.deleteAdmin(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void deleteAdmin_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.deleteAdmin(1L));

    assertTrue(exception.getMessage().contains("User not found"));
  }

  @Test
  void deleteAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
    User user = new User();
    user.setId(2L);
    user.setRole(Role.ROLE_NORMAL);

    when(userRepository.findById(2L)).thenReturn(Optional.of(user));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> superAdminService.deleteAdmin(2L));

    assertTrue(exception.getMessage().contains("User is not an admin"));
  }

  @Test
  void sendResetPasswordEmailToAdmin_ShouldSendEmail_WhenUserIsAdmin() {
    User admin = new User();
    admin.setEmail("admin@test.com");
    admin.setRole(Role.ROLE_ADMIN);

    when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

    superAdminService.sendResetPasswordEmailToAdmin("admin@test.com");

    verify(userService).sendResetPasswordLink("admin@test.com");
  }

  @Test
  void sendResetPasswordEmailToAdmin_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> superAdminService.sendResetPasswordEmailToAdmin("admin@test.com"));

    assertTrue(exception.getMessage().contains("User not found"));
  }

  @Test
  void sendResetPasswordEmailToAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
    User user = new User();
    user.setEmail("user@test.com");
    user.setRole(Role.ROLE_NORMAL);

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> superAdminService.sendResetPasswordEmailToAdmin("user@test.com"));

    assertTrue(exception.getMessage().contains("User is not an admin"));
  }
}
