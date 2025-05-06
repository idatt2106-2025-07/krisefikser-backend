package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.EmergencyGroupInvitation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmergencyGroupInvitationsRepoTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EmergencyGroupInvitationsRepo invitationsRepo;

  @Test
  @Rollback
  void addEmergencyGroupInvitation_shouldInsertNewInvitation() {
    EmergencyGroupInvitation newInvitation = new EmergencyGroupInvitation();
    newInvitation.setHouseholdId(3L);
    newInvitation.setGroupId(1L);

    invitationsRepo.addEmergencyGroupInvitation(newInvitation);

    Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM emergency_group_invitations WHERE household_id = ? AND emergency_group_id = ?",
            Integer.class,
            3L,
            1L
    );
    assertNotNull(count);
    assertEquals(1, count);
  }

  @Test
  @Rollback
  void deleteEmergencyGroupInvitation_shouldRemoveExistingInvitation() {
    int initialCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emergency_group_invitations WHERE household_id = 2 AND emergency_group_id = 2", Integer.class);

    int rowsAffected = invitationsRepo.deleteEmergencyGroupInvitation(2L, 2L);
    assertEquals(1, rowsAffected);

    Integer finalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM emergency_group_invitations WHERE household_id = 2 AND emergency_group_id = 2", Integer.class);
    assertEquals(initialCount - 1, finalCount);
  }

  @Test
  @Rollback
  void deleteEmergencyGroupInvitation_shouldReturnZeroIfInvitationNotFound() {
    int rowsAffected = invitationsRepo.deleteEmergencyGroupInvitation(999L, 999L);
    assertEquals(0, rowsAffected);
  }

  @Test
  void isInvitedToGroup_shouldReturnTrueIfExists() {
    assertTrue(invitationsRepo.isInvitedToGroup(2L, 4L));
  }

  @Test
  void isInvitedToGroup_shouldReturnFalseIfNotExists() {
    assertFalse(invitationsRepo.isInvitedToGroup(1L, 2L));
    assertFalse(invitationsRepo.isInvitedToGroup(2L, 1L));
    assertFalse(invitationsRepo.isInvitedToGroup(999L, 999L));
  }

  @Test
  void getInvitationsByHouseholdId_shouldReturnListOfInvitationsForExistingHousehold() {
    List<EmergencyGroupInvitation> invitations = invitationsRepo.getInvitationsByHouseholdId(2L);
    assertNotNull(invitations);
    assertEquals(2, invitations.size());
    assertEquals(2L, invitations.get(0).getHouseholdId());
    assertEquals(2L, invitations.get(0).getGroupId());
  }

  @Test
  void getInvitationsByHouseholdId_shouldReturnEmptyListForNonExistingHousehold() {
    List<EmergencyGroupInvitation> invitations = invitationsRepo.getInvitationsByHouseholdId(999L);
    assertNotNull(invitations);
    assertTrue(invitations.isEmpty());
  }
}