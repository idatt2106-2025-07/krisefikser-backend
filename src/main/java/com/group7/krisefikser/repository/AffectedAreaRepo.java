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
      area.setHighDangerRadiusKm(rs.getDouble("high_danger_radius_km"));
      area.setMediumDangerRadiusKm(rs.getDouble("medium_danger_radius_km"));
      area.setLowDangerRadiusKm(rs.getDouble("low_danger_radius_km"));
      area.setNotificationMessage(rs.getString("notification_message"));
      return area;
    });
  }
}
