package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.EmergencyGroupInvitation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing emergency group invitations in the database.
 * This class provides methods to add and delete emergency group invitations.
 */
@Repository
public class EmergencyGroupInvitationsRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for EmergencyGroupInvitationsRepo.
   *
   * @param jdbcTemplate the JdbcTemplate to be used for database operations
   */
  @Autowired
  public EmergencyGroupInvitationsRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Inserts a new emergency group invitation into the database.
   *
   * @param invitation the EmergencyGroupInvitation object to be inserted
   */
  public void addEmergencyGroupInvitation(EmergencyGroupInvitation invitation) {
    String sql = "INSERT INTO emergency_group_invitations (household_id, emergency_group_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, invitation.getHouseholdId(), invitation.getGroupId());
  }

  /**
   * Deletes an emergency group invitation from the database.
   *
   * @param householdId the ID of the user whose invitation is to be deleted
   * @param groupId     the ID of the emergency group
   */
  public int deleteEmergencyGroupInvitation(long householdId, long groupId) {
    String sql = "DELETE FROM emergency_group_invitations WHERE household_id = ? AND emergency_group_id = ?";
    return jdbcTemplate.update(sql, householdId, groupId);
  }

  /**
   * Checks if an emergency group invitation exists in the database.
   *
   * @param householdId the ID of the user to check
   * @param groupId     the ID of the emergency group
   * @return true if the invitation exists, false otherwise
   */
  public boolean isInvitedToGroup(long householdId, long groupId) {
    String sql = "SELECT COUNT(*) FROM emergency_group_invitations "
            + "WHERE household_id = ? AND emergency_group_id = ?";

    try {
      Integer count = jdbcTemplate.queryForObject(sql, Integer.class, householdId, groupId);
      return count != null && count > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }

  }

  /**
   * Retrieves all emergency group invitations for a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of emergency group invitations
   */
  public List<EmergencyGroupInvitation> getInvitationsByHouseholdId(long householdId) {
    String sql = "SELECT * FROM emergency_group_invitations WHERE household_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      EmergencyGroupInvitation invitation = new EmergencyGroupInvitation();
      invitation.setId(rs.getLong("id"));
      invitation.setHouseholdId(rs.getLong("household_id"));
      invitation.setGroupId(rs.getLong("emergency_group_id"));
      invitation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
      return invitation;
    }, householdId);
  }
}
