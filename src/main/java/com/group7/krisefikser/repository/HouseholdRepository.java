package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.Household;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
   * Constructor for injecting the JdbcTemplate dependency.
   *
   * @param jdbcTemplate the JdbcTemplate instance used for database operations
   */
  @Autowired
  public HouseholdRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Creates a new household in the database.
   * This method takes the name, longitude, and latitude of the household as parameters,
   * and inserts a new record into the households table.
   *
   * @param name the name of the household
   * @param longitude the longitude of the household
   * @param latitude the latitude of the household
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
    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{householdName}, Integer.class);
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
}
