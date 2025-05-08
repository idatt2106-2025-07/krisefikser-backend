package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.HouseholdInvitation;
import com.group7.krisefikser.utils.JwtUtils;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing household invitations.
 * This class provides methods to save, find, and delete household invitations.
 */
@Repository
@RequiredArgsConstructor
public class HouseholdInvitationRepository {
  private final JdbcTemplate jdbcTemplate;
  private final JwtUtils jwtUtils;

  private final RowMapper<HouseholdInvitation> rowMapper = (rs, rowNum) -> {
    HouseholdInvitation invitation = new HouseholdInvitation();
    invitation.setId(rs.getLong("id"));
    invitation.setHouseholdId(rs.getLong("household_id"));
    invitation.setInvitedByUserId(rs.getLong("invited_by_user_id"));
    invitation.setInvitedEmail(rs.getString("invited_email"));
    invitation.setInvitationToken(rs.getString("invitation_token"));
    invitation.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
    return invitation;
  };

  /**
   * Saves a new household invitation to the database.
   * If the invitation does not have an ID, it generates a new token and inserts the record.
   *
   * @param invitation The HouseholdInvitation object to save.
   * @return The saved HouseholdInvitation object with updated fields (e.g., ID, token, createdAt).
   */
  public HouseholdInvitation save(HouseholdInvitation invitation) {
    if (invitation.getId() == null) {
      String token = jwtUtils.generateInvitationToken(invitation.getInvitedEmail());
      LocalDateTime now = LocalDateTime.now();

      String sql = "INSERT INTO household_invitations (household_id, invited_by_user_id,"
          + " invited_email, invitation_token, created_at) VALUES (?, ?, ?, ?, ?)";

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, invitation.getHouseholdId());
        ps.setLong(2, invitation.getInvitedByUserId());
        ps.setString(3, invitation.getInvitedEmail());
        ps.setString(4, token);
        ps.setObject(5, now);
        return ps;
      }, keyHolder);

      // Extract the 'id' from the keys map instead of using getKey()
      Number id = (Number) keyHolder.getKeys().get("id");
      invitation.setId(id.longValue());
      invitation.setInvitationToken(token);
      invitation.setCreatedAt(now);
    }
    return invitation;
  }

  /**
   * Finds a household invitation by its token.
   *
   * @param token The unique token associated with the invitation.
   * @return An Optional containing the HouseholdInvitation if found, or empty if not found.
   */
  public Optional<HouseholdInvitation> findByToken(String token) {
    String sql = "SELECT * FROM household_invitations WHERE invitation_token = ?";
    List<HouseholdInvitation> results = jdbcTemplate.query(sql, rowMapper, token);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  /**
   * Finds all household invitations associated with a specific email.
   *
   * @param email The email address to search for.
   * @return A list of HouseholdInvitation objects associated with the given email.
   */
  public List<HouseholdInvitation> findByEmail(String email) {
    String sql = "SELECT * FROM household_invitations WHERE invited_email = ?";
    return jdbcTemplate.query(sql, rowMapper, email);
  }

  /**
   * Deletes a household invitation by its ID.
   *
   * @param id The ID of the invitation to delete.
   */
  public void delete(Long id) {
    jdbcTemplate.update("DELETE FROM household_invitations WHERE id = ?", id);
  }
}