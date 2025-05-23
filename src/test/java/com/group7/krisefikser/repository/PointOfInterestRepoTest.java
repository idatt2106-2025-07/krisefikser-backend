package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.model.location.PointOfInterest;
import com.group7.krisefikser.repository.location.PointOfInterestRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is a test class for the PointOfInterestRepo.
 * It tests the functionality of the methods in the PointOfInterestRepo class.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class PointOfInterestRepoTest {
  @Autowired
  private PointOfInterestRepo pointOfInterestRepo;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  /**
   * This method tests the getAllPointsOfInterest method in the PointOfInterestRepo class.
   * It retrieves all points of interest from the database and checks if the list is not null,
   * the size of the list is correct, and the attributes of the first point of interest are correct.
   */
  @Test
  void getAllPointsOfInterest() {
    List<PointOfInterest> pointsOfInterest = pointOfInterestRepo.getAllPointsOfInterest();
    assertNotNull(pointsOfInterest);
    assertEquals(6, pointsOfInterest.size());
    assertEquals(1, pointsOfInterest.get(0).getId());
    assertEquals(10.76, pointsOfInterest.get(0).getLongitude());
    assertEquals(59.91, pointsOfInterest.get(0).getLatitude());
    assertEquals("SHELTER", pointsOfInterest.get(0).getType().name());
  }

  /**
   * This method tests the getPointsOfInterestByTypes method in the PointOfInterestRepo class.
   * It retrieves points of interest based on their types from the database and checks if the list is not null,
   * the size of the list is correct, and the attributes of the first point of interest are correct.
   * It uses the PointOfInterestType enum to specify the types of points of interest to retrieve.
   */
  @Test
  void getPointsOfInterestByTypes() {
    List<PointOfInterest> pointsOfInterest = pointOfInterestRepo.getPointsOfInterestByTypes(List.of(
            PointOfInterestType.SHELTER,
            PointOfInterestType.FOOD_CENTRAL
    ));

    assertNotNull(pointsOfInterest);
    assertEquals(2, pointsOfInterest.size());
    assertEquals(1, pointsOfInterest.get(0).getId());
    assertEquals(10.76, pointsOfInterest.get(0).getLongitude());
    assertEquals(59.91, pointsOfInterest.get(0).getLatitude());
    assertEquals("SHELTER", pointsOfInterest.get(0).getType().name());
  }

  /**
   * This method tests the addPointOfInterest method in the PointOfInterestRepo class.
   * It creates a new PointOfInterest object, adds it to the database, and checks if the
   * ID of the new point is set correctly.
   */
  @Test
  void addPointOfInterest_shouldInsertNewPointAndSetId() {
    PointOfInterest newPoint = new PointOfInterest(
            null,
            63.4300,
            10.4000,
            PointOfInterestType.SHELTER,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "12345678",
            "General supplies available here"
    );

    pointOfInterestRepo.addPointOfInterest(newPoint);

    assertNotNull(newPoint.getId());
    assertTrue(newPoint.getId() > 0);
  }

  /**
   * This method tests the updatePointOfInterest method in the PointOfInterestRepo class.
   * It updates an existing point of interest in the database and checks if the update was successful.
   */
  @Test
  void deletePointOfInterest_shouldDeleteExistingPointAndReturnOne() {
    long existingId = 1L;

    String selectSqlBefore = "SELECT COUNT(*) FROM points_of_interest WHERE id = ?";
    Integer countBefore = jdbcTemplate.queryForObject(selectSqlBefore, Integer.class, existingId);
    assertEquals(1, countBefore);

    int rowsAffected = pointOfInterestRepo.deletePointOfInterest(existingId);
    assertEquals(1, rowsAffected);

    String selectSqlAfter = "SELECT COUNT(*) FROM points_of_interest WHERE id = ?";
    Integer countAfter = jdbcTemplate.queryForObject(selectSqlAfter, Integer.class, existingId);
    assertEquals(0, countAfter);
  }

  /**
   * This method tests the deletePointOfInterest method in the PointOfInterestRepo class.
   * It attempts to delete a point of interest that does not exist in the database and checks
   * if the deletion was handled correctly.
   */
  @Test
  void deletePointOfInterest_shouldReturnZeroForNonExistingId() {
    long nonExistingId = 999L;

    String selectSqlBefore = "SELECT COUNT(*) FROM points_of_interest WHERE id = ?";
    Integer countBefore = jdbcTemplate.queryForObject(selectSqlBefore, Integer.class, nonExistingId);
    assertEquals(0, countBefore);

    int rowsAffected = pointOfInterestRepo.deletePointOfInterest(nonExistingId);
    assertEquals(0, rowsAffected);

    String selectSqlAfter = "SELECT COUNT(*) FROM points_of_interest WHERE id = ?";
    Integer countAfter = jdbcTemplate.queryForObject(selectSqlAfter, Integer.class, nonExistingId);
    assertEquals(0, countAfter);
  }

  /**
   * This method tests the updatePointOfInterest method in the PointOfInterestRepo class.
   * It updates an existing point of interest in the database and checks if the update was
   * successful.
   */
  @Test
  void updatePointOfInterest_shouldUpdateExistingPointAndReturnOne() {
    long existingId = 3L;

    PointOfInterest updatedPoint = new PointOfInterest(
            existingId,
            63.5000,
            10.5000,
            PointOfInterestType.HOSPITAL,
            LocalTime.of(8, 0),
            LocalTime.of(20, 0),
            "98765432",
            "Updated hospital description"
    );

    int rowsAffected = pointOfInterestRepo.updatePointOfInterest(updatedPoint);
    assertEquals(1, rowsAffected);

    String selectSqlAfter = "SELECT * FROM points_of_interest WHERE id = 3";
    PointOfInterest pointAfter = jdbcTemplate.queryForObject(selectSqlAfter, (rs, rowNum) ->
            new PointOfInterest(
                    rs.getLong("id"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    PointOfInterestType.fromString(rs.getString("type")),
                    rs.getObject("opens_at", LocalTime.class),
                    rs.getObject("closes_at", LocalTime.class),
                    rs.getString("contact_number"),
                    rs.getString("description")
            ));

    assertEquals(updatedPoint.getId(), pointAfter.getId());
    assertEquals(updatedPoint.getLatitude(), pointAfter.getLatitude());
    assertEquals(updatedPoint.getLongitude(), pointAfter.getLongitude());
    assertEquals(updatedPoint.getType(), pointAfter.getType());
    assertEquals(updatedPoint.getOpensAt(), pointAfter.getOpensAt());
    assertEquals(updatedPoint.getClosesAt(), pointAfter.getClosesAt());
    assertEquals(updatedPoint.getContactNumber(), pointAfter.getContactNumber());
    assertEquals(updatedPoint.getDescription(), pointAfter.getDescription());
  }

  /**
   * This method tests the updatePointOfInterest method in the PointOfInterestRepo class.
   * It attempts to update a point of interest that does not exist in the database and checks
   * if the update was handled correctly.
   */
  @Test
  void updatePointOfInterest_shouldReturnZeroForNonExistingId() {
    long nonExistingId = 999L;
    PointOfInterest nonExistingPoint = new PointOfInterest(
            nonExistingId,
            63.5000,
            10.5000,
            PointOfInterestType.MEETING_PLACE,
            LocalTime.of(8, 0),
            LocalTime.of(20, 0),
            "98765432",
            "Updated hospital description"
    );

    int rowsAffected = pointOfInterestRepo.updatePointOfInterest(nonExistingPoint);
    assertEquals(0, rowsAffected);

    String selectAllSql = "SELECT COUNT(*) FROM points_of_interest";
    Integer totalCount = jdbcTemplate.queryForObject(selectAllSql, Integer.class);
    assertEquals(6, totalCount);
  }
}
