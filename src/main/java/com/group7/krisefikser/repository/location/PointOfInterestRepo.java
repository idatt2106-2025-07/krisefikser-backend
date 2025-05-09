package com.group7.krisefikser.repository.location;

import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.model.location.PointOfInterest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * This class is a repository for managing points of interest in the database.
 * It provides methods to get, add, and delete points of interest.
 * It uses JdbcTemplate to interact with the database.
 */
@Repository
public class PointOfInterestRepo {
  private final JdbcTemplate jdbcTemplate;
  private static final String OPENS_AT_COLUMN_NAME = "opens_at";
  private static final String CLOSES_AT_COLUMN_NAME = "closes_at";

  /**
   * Constructor for PointOfInterestRepo.
   *
   * @param jdbcTemplate The JdbcTemplate used to interact with the database.
   */

  @Autowired
  public PointOfInterestRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method retrieves all points of interest from the database.
   * It returns a list of PointOfInterest objects.
   */
  public List<PointOfInterest> getAllPointsOfInterest() {
    String sql = "SELECT * FROM points_of_interest";

    return jdbcTemplate.query(sql, (rs, rowNum) ->
            mapRowToPointOfInterest(rs));
  }

  /**
   * This method retrieves points of interest from the database based on their types.
   * It takes a list of PointOfInterestType enums as input and returns a list of
   * PointOfInterest objects.
   *
   * @param types A list of PointOfInterestType enums representing the types of
   *              points of interest to retrieve.
   * @return A list of PointOfInterest objects that match the specified types.
   */
  public List<PointOfInterest> getPointsOfInterestByTypes(List<PointOfInterestType> types) {
    String placeholders = String.join(",", Collections.nCopies(types.size(), "?"));
    String sql = "SELECT * FROM points_of_interest WHERE type IN (" + placeholders + ")";

    Object[] typeValues = types.stream()
            .map(PointOfInterestType::getType)
            .toArray();

    return jdbcTemplate.query(sql, (rs, rowNum) ->
            mapRowToPointOfInterest(rs), typeValues);
  }

  private PointOfInterest mapRowToPointOfInterest(ResultSet rs) throws SQLException {
    return new PointOfInterest(
            rs.getLong("id"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            PointOfInterestType.fromString(rs.getString("type")),
            rs.getTime(OPENS_AT_COLUMN_NAME) != null
                    ? rs.getTime(OPENS_AT_COLUMN_NAME).toLocalTime() : null,
            rs.getTime(CLOSES_AT_COLUMN_NAME) != null
                    ? rs.getTime(CLOSES_AT_COLUMN_NAME).toLocalTime() : null,
            rs.getString("contact_number"),
            rs.getString("description")
    );
  }

  /**
   * This method adds a new point of interest to the database.
   * It takes a PointOfInterest object as input and modifies the parameter to
   * include the id of the newly added point of interest.
   *
   * @param pointOfInterest The PointOfInterest object to add to the database.
   */
  public void addPointOfInterest(PointOfInterest pointOfInterest) {
    String sql = "INSERT INTO points_of_interest (latitude, longitude, type, "
            + "opens_at, closes_at, contact_number, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setDouble(1, pointOfInterest.getLatitude());
      ps.setDouble(2, pointOfInterest.getLongitude());
      ps.setString(3, pointOfInterest.getType().getType());
      ps.setObject(4, pointOfInterest.getOpensAt());
      ps.setObject(5, pointOfInterest.getClosesAt());
      ps.setString(6, pointOfInterest.getContactNumber());
      ps.setString(7, pointOfInterest.getDescription());
      return ps;
    }, keyHolder);

    Number newId = keyHolder.getKey();
    if (newId != null) {
      pointOfInterest.setId(newId.longValue());
    }
  }

  /**
   * This method deletes a point of interest from the database based on its ID.
   * It takes a long value representing the ID of the point of interest to delete.
   *
   * @param id The ID of the point of interest to delete.
   */
  public int deletePointOfInterest(long id) {
    String sql = "DELETE FROM points_of_interest WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  /**
   * This method updates an existing point of interest in the database.
   * It takes a PointOfInterest object as input and returns the number of rows affected.
   */
  public int updatePointOfInterest(PointOfInterest pointOfInterest) {
    String sql = "UPDATE points_of_interest SET latitude = ?, longitude = ?, type = ?, "
            + "opens_at = ?, closes_at = ?, contact_number = ?, description = ? WHERE id = ?";

    return jdbcTemplate.update(sql,
            pointOfInterest.getLatitude(),
            pointOfInterest.getLongitude(),
            pointOfInterest.getType().getType(),
            pointOfInterest.getOpensAt(),
            pointOfInterest.getClosesAt(),
            pointOfInterest.getContactNumber(),
            pointOfInterest.getDescription(),
            pointOfInterest.getId());
  }
}
