package com.group7.krisefikser.repository.location;

import com.group7.krisefikser.model.location.AffectedArea;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
      area.setName(rs.getString("name"));
      area.setLongitude(rs.getDouble("longitude"));
      area.setLatitude(rs.getDouble("latitude"));
      area.setHighDangerRadiusKm(rs.getDouble("high_danger_radius_km"));
      area.setMediumDangerRadiusKm(rs.getDouble("medium_danger_radius_km"));
      area.setLowDangerRadiusKm(rs.getDouble("low_danger_radius_km"));
      area.setSeverityLevel(rs.getInt("severity_level"));
      area.setDescription(rs.getString("description"));
      area.setStartDate(rs.getTimestamp("start_time").toLocalDateTime());
      return area;
    });
  }

  /**
   * Inserts a new affected area into the database.
   *
   * @param area the AffectedArea object to be inserted
   */
  public void addAffectedArea(AffectedArea area) {
    String sql = "INSERT INTO affected_areas (name, longitude, latitude, high_danger_radius_km, "
        + "medium_danger_radius_km, low_danger_radius_km, severity_level, description, "
        + "start_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, area.getName());
      ps.setDouble(2, area.getLongitude());
      ps.setDouble(3, area.getLatitude());
      ps.setDouble(4, area.getHighDangerRadiusKm());
      ps.setDouble(5, area.getMediumDangerRadiusKm());
      ps.setDouble(6, area.getLowDangerRadiusKm());
      ps.setInt(7, area.getSeverityLevel());
      ps.setString(8, area.getDescription());
      ps.setTimestamp(9, java.sql.Timestamp.valueOf(area.getStartDate()));
      return ps;
    }, keyHolder);
    area.setId(keyHolder.getKey().longValue());
  }

  /**
   * Deletes an affected area from the database by its ID.
   *
   * @param id the ID of the affected area to be deleted
   */
  public int deleteAffectedArea(long id) {
    String sql = "DELETE FROM affected_areas WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  /**
   * Updates an existing affected area in the database.
   *
   * @param area the AffectedArea object with updated values
   */
  public int updateAffectedArea(AffectedArea area) {
    String sql = "UPDATE affected_areas SET name = ?, longitude = ?, latitude = ?, "
            + "high_danger_radius_km = ?, medium_danger_radius_km = ?, "
            + "low_danger_radius_km = ?, severity_level = ?, description = ?, "
            + "start_time = ? WHERE id = ?";
    return jdbcTemplate.update(sql,
            area.getName(),
            area.getLongitude(),
            area.getLatitude(),
            area.getHighDangerRadiusKm(),
            area.getMediumDangerRadiusKm(),
            area.getLowDangerRadiusKm(),
            area.getSeverityLevel(),
            area.getDescription(),
            java.sql.Timestamp.valueOf(area.getStartDate()),
            area.getId());
  }
}
