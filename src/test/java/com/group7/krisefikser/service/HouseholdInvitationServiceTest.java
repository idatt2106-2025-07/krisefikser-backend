package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.group7.krisefikser.enums.EmailTemplateType;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.HouseholdInvitationRepository;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import com.group7.krisefikser.utils.JwtUtils;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the HouseholdInvitationService class.
 * This class tests the methods of the HouseholdInvitationService
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class HouseholdInvitationServiceTest {
  @Mock
  private HouseholdInvitationRepository invitationRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private HouseholdInvitationService invitationService;

  private final Long householdId = 1L;
  private final Long userId = 2L;
  private final String email = "test@example.com";
  private final String token = "test-invitation-token";
  private HouseholdInvitation invitation;

  @BeforeEach
  void setUp() {
    invitation = new HouseholdInvitation();
    invitation.setId(1L);
    invitation.setHouseholdId(householdId);
    invitation.setInvitedByUserId(userId);
    invitation.setInvitedEmail(email);
    invitation.setInvitationToken(token);
    invitation.setCreatedAt(LocalDateTime.now());
  }

  @Test
  void createInvitation_shouldCreateInvitationAndSendEmail() {
    // Mock user repository to return a user with the expected householdId
    User user = new User();
    user.setId(userId);
    user.setHouseholdId(householdId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Mock invitation repository save
    when(invitationRepository.save(any(HouseholdInvitation.class))).thenAnswer(invocation -> {
      HouseholdInvitation inv = invocation.getArgument(0);
      inv.setId(1L);
      inv.setInvitationToken(token);
      inv.setCreatedAt(LocalDateTime.now());
      return inv;
    });

    HouseholdInvitation result = invitationService.createInvitation(userId, email);

    assertNotNull(result);
    assertEquals(householdId, result.getHouseholdId());
    assertEquals(userId, result.getInvitedByUserId());
    assertEquals(email, result.getInvitedEmail());

    verify(invitationRepository).save(any(HouseholdInvitation.class));
    verify(userRepository).findById(userId);

    ArgumentCaptor<Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(emailService).sendTemplateMessage(
      eq(email),
      eq(EmailTemplateType.HOUSEHOLD_INVITE),
      paramsCaptor.capture()
    );

    Map<String, String> emailParams = paramsCaptor.getValue();
    assertTrue(emailParams.containsKey("inviteLink"));
    assertTrue(emailParams.get("inviteLink").contains(token));
  }

  @Test
  void acceptInvitation_shouldAddUserToHouseholdAndDeleteInvitation() throws JwtMissingPropertyException {
    when(jwtUtils.validateInvitationTokenAndGetEmail(token)).thenReturn(email);
    when(invitationRepository.findByToken(token)).thenReturn(Optional.of(invitation));

    Long result = invitationService.acceptInvitation(token, userId);

    assertEquals(householdId, result);

    verify(jwtUtils).validateInvitationTokenAndGetEmail(token);

    verify(userRepository).updateUserHousehold(userId, householdId);

    verify(invitationRepository).delete(invitation.getId());
  }

  @Test
  void acceptInvitation_shouldThrowExceptionWhenInvitationNotFound() throws JwtMissingPropertyException {
    when(jwtUtils.validateInvitationTokenAndGetEmail(token)).thenReturn(email);
    when(invitationRepository.findByToken(token)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () ->
      invitationService.acceptInvitation(token, userId));

    assertEquals("Invitation not found in database", exception.getMessage());
    verify(jwtUtils).validateInvitationTokenAndGetEmail(token);
    verify(invitationRepository).findByToken(token);
    verify(userRepository, never()).updateUserHousehold(any(), any());
  }

  @Test
  void acceptInvitation_shouldThrowExceptionWhenTokenIsInvalid() throws JwtMissingPropertyException {
    when(jwtUtils.validateInvitationTokenAndGetEmail(token))
      .thenThrow(new JWTVerificationException("Invalid token"));

    Exception exception = assertThrows(RuntimeException.class, () ->
      invitationService.acceptInvitation(token, userId));

    assertEquals("Invalid or expired invitation token", exception.getMessage());
    verify(jwtUtils).validateInvitationTokenAndGetEmail(token);
    verify(invitationRepository, never()).findByToken(anyString());
    verify(userRepository, never()).updateUserHousehold(any(), any());
  }

  @Test
  void acceptInvitation_shouldThrowExceptionWhenTokenHasNoEmail() throws JwtMissingPropertyException {
    when(jwtUtils.validateInvitationTokenAndGetEmail(token))
      .thenThrow(new JwtMissingPropertyException("Token does not contain an email"));

    Exception exception = assertThrows(RuntimeException.class, () ->
      invitationService.acceptInvitation(token, userId));

    assertEquals("Invalid or expired invitation token", exception.getMessage());
    verify(jwtUtils).validateInvitationTokenAndGetEmail(token);
    verify(invitationRepository, never()).findByToken(anyString());
    verify(userRepository, never()).updateUserHousehold(any(), any());
  }
}