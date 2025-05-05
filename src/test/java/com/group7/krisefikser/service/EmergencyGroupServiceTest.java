package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.model.EmergencyGroup;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.group7.krisefikser.model.EmergencyGroupInvitation;
import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.User;
import com.group7.krisefikser.repository.EmergencyGroupInvitationsRepo;
import com.group7.krisefikser.repository.EmergencyGroupRepo;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class EmergencyGroupServiceTest {

  @Mock
  private EmergencyGroupRepo emergencyGroupRepo;

  @Mock
  private HouseholdRepository householdRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private EmergencyGroupInvitationsRepo emergencyGroupInvitationsRepo;

  @InjectMocks
  private EmergencyGroupService emergencyGroupService;

  private EmergencyGroup testEmergencyGroup;
  private EmergencyGroupRequest testEmergencyGroupRequest;
  private Date createdAt;
  private User testUser;
  private Household testHousehold;
  private Household householdToInvite;

  @BeforeEach
  void setUp() {
    createdAt = Date.valueOf(LocalDateTime.now().toLocalDate());

    testEmergencyGroup = new EmergencyGroup();
    testEmergencyGroup.setId(1L);
    testEmergencyGroup.setName("Fire Department");
    testEmergencyGroup.setCreatedAt(createdAt);

    testEmergencyGroupRequest = new EmergencyGroupRequest();
    testEmergencyGroupRequest.setName("Fire Department");

    testUser = new User();
    testUser.setId(100L);
    testUser.setHouseholdId(200L);

    testHousehold = new Household();
    testHousehold.setId(200L);
    testHousehold.setName("My Household");

    householdToInvite = new Household();
    householdToInvite.setId(300L);
    householdToInvite.setName("Neighbor's Household");
  }

  @Test
  void getEmergencyGroupById_Success() {
    when(emergencyGroupRepo.getEmergencyGroupById(1L)).thenReturn(testEmergencyGroup);

    EmergencyGroupResponse response = emergencyGroupService.getEmergencyGroupById(1L);

    assertNotNull(response);
    assertEquals(testEmergencyGroup.getId(), response.getId());
    assertEquals(testEmergencyGroup.getName(), response.getName());
    assertEquals(testEmergencyGroup.getCreatedAt().toString(), response.getCreatedAt());
    verify(emergencyGroupRepo, times(1)).getEmergencyGroupById(1L);
  }

  @Test
  void getEmergencyGroupById_NotFound() {
    when(emergencyGroupRepo.getEmergencyGroupById(999L))
            .thenThrow(new EmptyResultDataAccessException(1));

    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            emergencyGroupService.getEmergencyGroupById(999L));

    assertEquals("Emergency group with ID 999 not found.", exception.getMessage());
    verify(emergencyGroupRepo, times(1)).getEmergencyGroupById(999L);
  }


  @Test
  void addEmergencyGroup_Success() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    doAnswer(invocation -> {
      EmergencyGroup group = invocation.getArgument(0);
      group.setId(1L);
      group.setCreatedAt(createdAt);
      return null;
    }).when(emergencyGroupRepo).addEmergencyGroup(any(EmergencyGroup.class));
    doNothing().when(householdRepository).addHouseholdToGroup(anyLong(), anyLong());

    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));

    EmergencyGroupResponse response = emergencyGroupService.addEmergencyGroup(testEmergencyGroupRequest);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(testEmergencyGroupRequest.getName(), response.getName());
    assertNotNull(response.getCreatedAt());
    verify(emergencyGroupRepo, times(1)).addEmergencyGroup(any(EmergencyGroup.class));
  }

  @Test
  void addEmergencyGroup_NameTaken() {
    doThrow(new DataIntegrityViolationException("Name already taken"))
            .when(emergencyGroupRepo).addEmergencyGroup(any(EmergencyGroup.class));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      emergencyGroupService.addEmergencyGroup(testEmergencyGroupRequest));

    assertEquals("Failed to add emergency group. Name already taken.", exception.getMessage());
    verify(emergencyGroupRepo, times(1)).addEmergencyGroup(any(EmergencyGroup.class));
  }

  @Test
  void inviteHouseholdByName_successfulInvitation() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(householdRepository.getHouseholdByName("Neighbor's Household")).thenReturn(Optional.of(householdToInvite));
    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
    when(householdRepository.getHouseholdById(200L)).thenReturn(Optional.of(testHousehold));

    emergencyGroupService.inviteHouseholdByName("Neighbor's Household");

    verify(emergencyGroupInvitationsRepo, times(1)).addEmergencyGroupInvitation(any(EmergencyGroupInvitation.class));
  }

  @Test
  void inviteHouseholdByName_householdNotFound() {
    when(householdRepository.getHouseholdByName("NonExistentHousehold")).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> emergencyGroupService.inviteHouseholdByName("NonExistentHousehold"));

    verify(emergencyGroupInvitationsRepo, never()).addEmergencyGroupInvitation(any());
  }

  @Test
  void inviteHouseholdByName_userNotFound() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(householdRepository.getHouseholdByName("Neighbor's Household")).thenReturn(Optional.of(householdToInvite));
    when(userRepository.findById(100L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> emergencyGroupService.inviteHouseholdByName("Neighbor's Household"));

    verify(emergencyGroupInvitationsRepo, never()).addEmergencyGroupInvitation(any());
  }

  @Test
  void inviteHouseholdByName_requestingHouseholdNotFound() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(householdRepository.getHouseholdByName("Neighbor's Household")).thenReturn(Optional.of(householdToInvite));
    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
    when(householdRepository.getHouseholdById(200L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> emergencyGroupService.inviteHouseholdByName("Neighbor's Household"));

    verify(emergencyGroupInvitationsRepo, never()).addEmergencyGroupInvitation(any());
  }

  @Test
  void answerEmergencyGroupInvitation_acceptInvitation() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");
    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
    when(emergencyGroupInvitationsRepo.isInvitedToGroup(200L, 50L)).thenReturn(true);

    emergencyGroupService.answerEmergencyGroupInvitation(50L, true);

    verify(householdRepository, times(1)).addHouseholdToGroup(200L, 50L);
    verify(emergencyGroupInvitationsRepo, times(1)).deleteEmergencyGroupInvitation(200L, 50L);
  }

  @Test
  void answerEmergencyGroupInvitation_declineInvitation() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");
    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
    when(emergencyGroupInvitationsRepo.isInvitedToGroup(200L, 50L)).thenReturn(true);

    emergencyGroupService.answerEmergencyGroupInvitation(50L, false);

    verify(householdRepository, never()).addHouseholdToGroup(anyLong(), anyLong());
    verify(emergencyGroupInvitationsRepo, times(1)).deleteEmergencyGroupInvitation(200L, 50L);
  }

  @Test
  void answerEmergencyGroupInvitation_userNotFound() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");
    when(userRepository.findById(100L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> emergencyGroupService.answerEmergencyGroupInvitation(50L, true));

    verify(householdRepository, never()).addHouseholdToGroup(anyLong(), anyLong());
    verify(emergencyGroupInvitationsRepo, never()).deleteEmergencyGroupInvitation(anyLong(), anyLong());
  }

  @Test
  void answerEmergencyGroupInvitation_notInvited() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");

    when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("100");
    when(userRepository.findById(100L)).thenReturn(Optional.of(testUser));
    when(emergencyGroupInvitationsRepo.isInvitedToGroup(200L, 50L)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> emergencyGroupService.answerEmergencyGroupInvitation(50L, true));

    verify(householdRepository, never()).addHouseholdToGroup(anyLong(), anyLong());
    verify(emergencyGroupInvitationsRepo, never()).deleteEmergencyGroupInvitation(anyLong(), anyLong());
  }
}
