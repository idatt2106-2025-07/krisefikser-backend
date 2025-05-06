package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.EmergencyGroup;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Repository class for accessing emergency group data from the database.
 * This class contains methods to add and retrieve emergency groups.
 * It uses JdbcTemplate for database operations.
 */
@Repository
public class EmergencyGroupRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for EmergencyGroupRepo.
   *
   * @param jdbcTemplate the JdbcTemplate to be used for database operations
   */
  @Autowired
  public EmergencyGroupRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Inserts a new emergency group into the database.
   *
   * @param emergencyGroup the name of the emergency group
   */
  public void addEmergencyGroup(EmergencyGroup emergencyGroup) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql;

    sql = "INSERT INTO emergency_groups (name) VALUES (?)";
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, emergencyGroup.getName());
      return ps;
    }, keyHolder);

    Number key = (Number) keyHolder.getKeyList().get(0).get("id");
    if (key != null) {
      emergencyGroup.setId(key.longValue());
    }
  }

  /**
   * Fetches a specific emergency group from the database by its ID.
   *
   * @param id the ID of the emergency group
   * @return the EmergencyGroup object with the specified ID
   */
  public EmergencyGroup getEmergencyGroupById(Long id) {
    String sql = "SELECT * FROM emergency_groups WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
      EmergencyGroup group = new EmergencyGroup();
      group.setId(rs.getLong("id"));
      group.setName(rs.getString("name"));
      group.setCreatedAt(rs.getDate("created_at"));
      return group;
    }, id);
  }
}
