package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.group7.krisefikser.dto.request.household.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.household.EmergencyGroupInvitationResponse;
import com.group7.krisefikser.dto.response.household.EmergencyGroupResponse;
import com.group7.krisefikser.model.household.EmergencyGroup;

import com.group7.krisefikser.service.household.EmergencyGroupService;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.group7.krisefikser.model.household.EmergencyGroupInvitation;
import com.group7.krisefikser.model.household.Household;
import com.group7.krisefikser.model.user.User;
import com.group7.krisefikser.repository.household.EmergencyGroupInvitationsRepo;
import com.group7.krisefikser.repository.household.EmergencyGroupRepo;
import com.group7.krisefikser.repository.household.HouseholdRepository;
import com.group7.krisefikser.repository.user.UserRepository;
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
  private LocalDateTime createdAtDateTime;
  private User testUser;
  private Household testHousehold;
  private Household householdToInvite;

  @BeforeEach
  void setUp() {
    createdAt = Date.valueOf(LocalDateTime.now().toLocalDate());
    createdAtDateTime = LocalDateTime.now();

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
    when(householdRepository.getEmergencyIdByHouseholdId(200L)).thenReturn(1L); // Mock group ID

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

    // The household to invite exists
    when(householdRepository.getHouseholdByName("Neighbor's Household")).thenReturn(Optional.of(householdToInvite));

    // But the requesting user is not found in the system
    when(userRepository.findById(100L)).thenReturn(Optional.empty());

    // Should throw due to user not being found
    assertThrows(NoSuchElementException.class, () ->
      emergencyGroupService.inviteHouseholdByName("Neighbor's Household")
    );

    // Verify no invitation was created
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

  @Test
  void getEmergencyGroupInvitationsForCurrentUser_shouldReturnListOfInvitations_whenInvitationsExist() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn(String.valueOf(testUser.getId()));
    when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));


    EmergencyGroupInvitation invitation1 = new EmergencyGroupInvitation();
    invitation1.setId(1L);
    invitation1.setHouseholdId(testHousehold.getId());
    invitation1.setGroupId(10L);
    invitation1.setCreatedAt(createdAtDateTime);

    EmergencyGroupInvitation invitation2 = new EmergencyGroupInvitation();
    invitation2.setId(2L);
    invitation2.setHouseholdId(testHousehold.getId());
    invitation2.setGroupId(11L);
    invitation2.setCreatedAt(createdAtDateTime);

    List<EmergencyGroupInvitation> invitations = List.of(invitation1, invitation2);
    when(emergencyGroupInvitationsRepo.getInvitationsByHouseholdId(testHousehold.getId())).thenReturn(invitations);

    EmergencyGroup group1 = new EmergencyGroup();
    group1.setId(10L);
    group1.setName("First Responders");
    when(emergencyGroupRepo.getEmergencyGroupById(10L)).thenReturn(group1);

    EmergencyGroup group2 = new EmergencyGroup();
    group2.setId(11L);
    group2.setName("Medical Team");
    when(emergencyGroupRepo.getEmergencyGroupById(11L)).thenReturn(group2);

    List<EmergencyGroupInvitationResponse> responses = emergencyGroupService.getEmergencyGroupInvitationsForCurrentUser();

    assertNotNull(responses);
    assertEquals(2, responses.size());

    EmergencyGroupInvitationResponse response1 = responses.get(0);
    assertEquals(1L, response1.getId());
    assertEquals(testHousehold.getId(), response1.getHouseholdId());
    assertEquals(10L, response1.getGroupId());
    assertEquals(createdAtDateTime.toString(), response1.getCreatedAt());
    assertEquals("First Responders", response1.getGroupName());

    EmergencyGroupInvitationResponse response2 = responses.get(1);
    assertEquals(2L, response2.getId());
    assertEquals(testHousehold.getId(), response2.getHouseholdId());
    assertEquals(11L, response2.getGroupId());
    assertEquals(createdAtDateTime.toString(), response2.getCreatedAt());
    assertEquals("Medical Team", response2.getGroupName());
  }

  @Test
  void getEmergencyGroupInvitationsForCurrentUser_shouldReturnEmptyList_whenNoInvitationsExist() {
   SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn(String.valueOf(testUser.getId()));
    when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

    when(emergencyGroupInvitationsRepo.getInvitationsByHouseholdId(testHousehold.getId())).thenReturn(List.of());

    List<EmergencyGroupInvitationResponse> responses = emergencyGroupService.getEmergencyGroupInvitationsForCurrentUser();

    assertNotNull(responses);
    assertTrue(responses.isEmpty());
  }
}
