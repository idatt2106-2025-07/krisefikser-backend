package com.group7.krisefikser.repository;

import com.group7.krisefikser.dto.request.JoinHouseholdRequest;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing JoinHouseholdRequest entities in the database.
 * Provides methods for saving, retrieving, and deleting join household requests.
 */
@Repository
public class JoinHouseholdRequestRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * RowMapper implementation for mapping database rows to JoinHouseholdRequest objects.
   */
  private final RowMapper<JoinHouseholdRequest> rowMapper = (
      rs, rowNum) -> new JoinHouseholdRequest(
      rs.getLong("id"),
      rs.getLong("household_id"),
      rs.getLong("user_id"));

  /**
   * Constructor for injecting the JdbcTemplate dependency.
   *
   * @param jdbcTemplate the JdbcTemplate instance used for database operations
   */
  @Autowired
  public JoinHouseholdRequestRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Saves a new JoinHouseholdRequest to the database.
   *
   * @param request the JoinHouseholdRequest object to save
   * @return the saved JoinHouseholdRequest object with the generated ID
   */
  public JoinHouseholdRequest save(JoinHouseholdRequest request) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(conn -> {
      PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO join_household_requests (household_id, user_id) VALUES (?, ?)",
          Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, request.getHouseholdId());
      ps.setLong(2, request.getUserId());
      return ps;
    }, keyHolder);
    request.setId(keyHolder.getKey().longValue());
    return request;
  }

  /**
   * Retrieves a list of JoinHouseholdRequest objects by household ID.
   *
   * @param householdId the ID of the household
   * @return a list of JoinHouseholdRequest objects associated with the given household ID
   */
  public List<JoinHouseholdRequest> findByHouseholdId(Long householdId) {
    return jdbcTemplate.query(
      "SELECT * FROM join_household_requests WHERE household_id = ?",
      rowMapper, householdId);
  }

  /**
   * Retrieves a JoinHouseholdRequest by its ID.
   *
   * @param id the ID of the join request
   * @return the JoinHouseholdRequest object with the given ID
   */
  public JoinHouseholdRequest findById(Long id) {
    return jdbcTemplate.queryForObject(
      "SELECT * FROM join_household_requests WHERE id = ?",
      rowMapper, id);
  }

  /**
   * Deletes a JoinHouseholdRequest from the database by its ID.
   *
   * @param id the ID of the join request to delete
   */
  public void deleteById(Long id) {
    jdbcTemplate.update("DELETE FROM join_household_requests WHERE id = ?", id);
  }
}