package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.household.EmergencyGroup;
import com.group7.krisefikser.repository.household.EmergencyGroupRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmergencyGroupRepoTest {
  @Autowired
  private EmergencyGroupRepo emergencyGroupRepo;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void getEmergencyGroupById_existingId_groupA() {
    EmergencyGroup fetchedGroup = emergencyGroupRepo.getEmergencyGroupById(1L);

    assertNotNull(fetchedGroup);
    assertEquals(1L, fetchedGroup.getId());
    assertEquals("Group A", fetchedGroup.getName());
    assertNotNull(fetchedGroup.getCreatedAt());
  }

  @Test
  void getEmergencyGroupById_nonExistingId() {
    assertThrows(EmptyResultDataAccessException.class, () -> emergencyGroupRepo.getEmergencyGroupById(999L));
  }

  @Test
  @Rollback
  void addEmergencyGroup_withCreatedAt() {
    EmergencyGroup group = new EmergencyGroup();
    group.setName("New Group");
    group.setCreatedAt(Date.valueOf(LocalDate.now()));
    emergencyGroupRepo.addEmergencyGroup(group);

    Long newGroupId = jdbcTemplate.queryForObject("SELECT id FROM emergency_groups WHERE name = ?", Long.class, "New Group");
    EmergencyGroup fetchedGroup = emergencyGroupRepo.getEmergencyGroupById(newGroupId);

    assertEquals(newGroupId, group.getId());
    assertNotNull(fetchedGroup);
    assertEquals("New Group", fetchedGroup.getName());
    assertEquals(Date.valueOf(LocalDate.now()), fetchedGroup.getCreatedAt());
  }

  @Test
  @Rollback
  void addEmergencyGroup_withoutCreatedAt() {
    EmergencyGroup group = new EmergencyGroup();
    group.setName("Another New Group");
    emergencyGroupRepo.addEmergencyGroup(group);

    Long newGroupId = jdbcTemplate.queryForObject("SELECT id FROM emergency_groups WHERE name = ?", Long.class, "Another New Group");
    EmergencyGroup fetchedGroup = emergencyGroupRepo.getEmergencyGroupById(newGroupId);

    assertEquals(newGroupId, group.getId());
    assertNotNull(fetchedGroup);
    assertEquals("Another New Group", fetchedGroup.getName());
    assertNotNull(fetchedGroup.getCreatedAt());
  }
}
