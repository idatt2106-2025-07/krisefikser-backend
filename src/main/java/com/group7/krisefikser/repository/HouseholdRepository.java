package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.Household;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing Household entities in the database.
 * Provides methods for saving and retrieving household information.
 */
@Repository
public class HouseholdRepository {
  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Household> rowMapper = (rs, rowNum) -> new Household(
      rs.getLong("id"),
      rs.getString("name"),
      rs.getDouble("longitude"),
      rs.getDouble("latitude")
  );

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