package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.AffectedArea;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for accessing affected area data from the database.
 */
@Repository
public class AffectedAreaRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for AffectedAreaRepo.
   *
   * @param jdbcTemplate the JdbcTemplate to be used for database operations
   */
  @Autowired
  public AffectedAreaRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Fetches all affected areas from the database and maps them to AffectedArea objects.
   *
   * @return a list of AffectedArea objects
   */
  public List<AffectedArea> getAllAffectedAreas() {
    String sql = "SELECT * FROM affected_areas";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      AffectedArea area = new AffectedArea();
      area.setId(rs.getLong("id"));
      area.setLongitude(rs.getDouble("longitude"));
      area.setLatitude(rs.getDouble("latitude"));
      area.setDangerRadiusKm(rs.getDouble("danger_radius_km"));
      area.setSeverityLevel(rs.getInt("severity_level"));
      area.setDescription(rs.getString("description"));
      area.setStartDate(rs.getTimestamp("start_time").toLocalDateTime());
      return area;
    });
  }
}
