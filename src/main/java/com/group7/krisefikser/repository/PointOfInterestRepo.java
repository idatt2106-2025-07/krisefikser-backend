package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.PointOfInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.group7.krisefikser.enums.PointOfInterestType;

import java.util.List;

/**
 * This class is a repository for managing points of interest in the database.
 * It provides methods to get, add, and delete points of interest.
 * It uses JdbcTemplate to interact with the database.
 */
@Repository
public class PointOfInterestRepo {
  private final JdbcTemplate jdbcTemplate;

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
                    PointOfInterestType.fromString(rs.getString("type"))
            ));
  }
}
