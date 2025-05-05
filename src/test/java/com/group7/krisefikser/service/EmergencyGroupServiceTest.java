package com.group7.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.model.EmergencyGroup;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import com.group7.krisefikser.repository.EmergencyGroupRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class EmergencyGroupServiceTest {

  @Mock
  private EmergencyGroupRepo emergencyGroupRepo;

  @InjectMocks
  private EmergencyGroupService emergencyGroupService;

  private EmergencyGroup testEmergencyGroup;
  private EmergencyGroupRequest testEmergencyGroupRequest;
  private Date createdAt;

  @BeforeEach
  void setUp() {
    createdAt = Date.valueOf(LocalDateTime.now().toLocalDate());

    testEmergencyGroup = new EmergencyGroup();
    testEmergencyGroup.setId(1L);
    testEmergencyGroup.setName("Fire Department");
    testEmergencyGroup.setCreatedAt(createdAt);

    testEmergencyGroupRequest = new EmergencyGroupRequest();
    testEmergencyGroupRequest.setName("Fire Department");
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

    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
      emergencyGroupService.getEmergencyGroupById(999L);
    });

    assertEquals("Emergency group with ID 999 not found.", exception.getMessage());
    verify(emergencyGroupRepo, times(1)).getEmergencyGroupById(999L);
  }


  @Test
  void addEmergencyGroup_Success() {
    doAnswer(invocation -> {
      EmergencyGroup group = invocation.getArgument(0);
      group.setId(1L);
      group.setCreatedAt(createdAt);
      return null;
    }).when(emergencyGroupRepo).addEmergencyGroup(any(EmergencyGroup.class));

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

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      emergencyGroupService.addEmergencyGroup(testEmergencyGroupRequest);
    });

    assertEquals("Failed to add emergency group. Name already taken.", exception.getMessage());
    verify(emergencyGroupRepo, times(1)).addEmergencyGroup(any(EmergencyGroup.class));
  }
}
