package com.group7.krisefikser.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@RequiredArgsConstructor
public class HouseholdRepository {
  private final JdbcTemplate jdbcTemplate;

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

  public boolean existsByName(String householdName) {
    String sql = "SELECT COUNT(*) FROM households WHERE name = ?";
    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{householdName}, Integer.class);
    return count != null && count > 0;
  }
}
