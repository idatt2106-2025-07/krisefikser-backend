package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.model.PointOfInterest;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
            new PointOfInterest(
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
            ));
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
            new PointOfInterest(
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
            ), typeValues);
  }
}
