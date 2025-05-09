package com.group7.krisefikser.repository.household;

import com.group7.krisefikser.dto.response.household.HouseholdMemberResponse;
import com.group7.krisefikser.dto.response.household.NonUserMemberResponse;
import com.group7.krisefikser.model.household.Household;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * This class is responsible for interacting with the database to perform CRUD operations
 * related to households.
 * It uses JdbcTemplate to execute SQL queries and manage database connections.
 */
@Repository
@RequiredArgsConstructor
public class HouseholdRepository {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Creates a new household in the database.
   * This method takes the name, longitude, and latitude of the household as parameters,
   * and inserts a new record into the households table.
   *
   * @param name      the name of the household
   * @param longitude the longitude of the household
   * @param latitude  the latitude of the household
   * @return the ID of the newly created household
   */
  public Long createHousehold(String name, double longitude, double latitude) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO households (name, longitude, latitude) VALUES (?, ?, ?)",
              Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, name);
      ps.setDouble(2, longitude);
      ps.setDouble(3, latitude);
      return ps;
    }, keyHolder);
    return keyHolder.getKey().longValue();
  }

  /**
   * Checks if a household with the given name already exists in the database.
   * This method executes a SQL query to count the number of records
   * with the specified name in the households table.
   * If the count is greater than 0, it means the household exists.
   *
   * @param householdName the name of the household to check
   * @return true if the household exists, false otherwise
   */
  public boolean existsByName(String householdName) {
    String sql = "SELECT COUNT(*) FROM households WHERE name = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, householdName);
    return count != null && count > 0;
  }

  /**
   * Saves a new Household to the database.
   *
   * @param household the Household object to save
   * @return the saved Household object with the generated ID
   */
  public Household save(Household household) {
    SimpleJdbcInsert householdInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("households")
            .usingGeneratedKeyColumns("id");

    Map<String, Object> params = new HashMap<>();
    params.put("name", household.getName());
    params.put("longitude", household.getLongitude());
    params.put("latitude", household.getLatitude());

    Number householdId = householdInsert.executeAndReturnKey(params);
    household.setId(householdId.longValue());

    return household;
  }

  /**
   * Retrieves a Household by its name from the database.
   *
   * @param name the name of the household to retrieve
   * @return the Household object with the specified name
   */
  public Optional<Household> getHouseholdByName(String name) {
    String sql = "SELECT * FROM households WHERE name = ?";
    try {
      return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
        mapRowToHousehold(rs), name));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * Retrieves a Household by its ID from the database.
   *
   * @param id the ID of the household to retrieve
   * @return the Household object with the specified ID
   */
  public Optional<Household> getHouseholdById(Long id) {
    String sql = "SELECT * FROM households WHERE id = ?";
    try {
      return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
              mapRowToHousehold(rs), id));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  private Household mapRowToHousehold(ResultSet rs) throws SQLException {
    Household household = new Household();
    household.setId(rs.getLong("id"));
    household.setName(rs.getString("name"));
    household.setLongitude(rs.getDouble("longitude"));
    household.setLatitude(rs.getDouble("latitude"));
    household.setEmergencyGroupId(rs.getLong("emergency_group_id"));
    return household;
  }

  /**
   * Updates the household ID of a user in the database.
   * This method is used to associate a user with a specific household.
   *
   * @param householdId  the ID of the household to associate with the user
   * @param groupId      the ID of the group to associate with the user
   */
  public void addHouseholdToGroup(long householdId, long groupId) {
    String sql = "UPDATE households SET emergency_group_id = ? WHERE id = ?";
    jdbcTemplate.update(sql, groupId, householdId);
  }

  /**
   * Finds all user members of a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of HouseholdMemberResponse objects
   */
  public List<HouseholdMemberResponse> findMembersByHouseholdId(Long householdId) {
    String sql = "SELECT id, name, email FROM users WHERE household_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      HouseholdMemberResponse member = new HouseholdMemberResponse();
      member.setId(rs.getLong("id"));
      member.setName(rs.getString("name"));
      member.setEmail(rs.getString("email"));
      return member;
    }, householdId);
  }

  /**
   * Finds all non-user members of a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of NonUserMemberResponse objects
   */
  public List<NonUserMemberResponse> findNonUserMembersByHouseholdId(Long householdId) {
    String sql = "SELECT id, name, type FROM non_user_members WHERE household_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      NonUserMemberResponse member = new NonUserMemberResponse();
      member.setId(rs.getLong("id"));
      member.setName(rs.getString("name"));
      member.setType(rs.getString("type"));
      return member;
    }, householdId);
  }

  /**
   * Retrieves the emergency group ID for a specific household.
   *
   * @param householdId the ID of the household
   * @return the emergency group ID associated with the household
   */
  public Long getEmergencyIdByHouseholdId(Long householdId) {
    String sql = "SELECT emergency_group_id FROM households WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, Long.class, householdId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }
}
