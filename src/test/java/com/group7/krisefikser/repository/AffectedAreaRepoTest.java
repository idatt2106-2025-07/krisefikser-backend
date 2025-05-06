package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.AffectedArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the AffectedAreaRepo.
 * This class is used to test the AffectedAreaRepo functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
class AffectedAreaRepoTest {
  @Autowired
  private AffectedAreaRepo affectedAreaRepo;

  @Autowired
  private JdbcTemplate jdbcTemplate;
  private AffectedArea testArea1;
  private AffectedArea testArea2;

  @BeforeEach
  void setUp() {
    testArea1 = new AffectedArea();
    testArea1.setName("Name 1");
    testArea1.setLongitude(11.5);
    testArea1.setLatitude(63.5);
    testArea1.setHighDangerRadiusKm(6.0);
    testArea1.setMediumDangerRadiusKm(11.0);
    testArea1.setLowDangerRadiusKm(16.0);
    testArea1.setSeverityLevel(2);
    testArea1.setDescription("Test Area 1");
    testArea1.setStartDate(LocalDateTime.now().minusDays(2));

    testArea2 = new AffectedArea();
    testArea2.setName("Name 2");
    testArea2.setLongitude(12.0);
    testArea2.setLatitude(64.0);
    testArea2.setHighDangerRadiusKm(7.0);
    testArea2.setMediumDangerRadiusKm(12.0);
    testArea2.setLowDangerRadiusKm(17.0);
    testArea2.setSeverityLevel(1);
    testArea2.setDescription("Test Area 2");
    testArea2.setStartDate(LocalDateTime.now().minusDays(1));
  }

  /**
   * Test method to verify the retrieval of all affected areas from the database.
   * It checks if the list is not null and contains the expected number of elements.
   * It also verifies that the first element has the expected values for its fields.
   */
  @Test
  void getAllAffectedAreas() {
    List<AffectedArea> affectedAreaList = affectedAreaRepo.getAllAffectedAreas();

    assertNotNull(affectedAreaList);
    assertEquals(3, affectedAreaList.size());
    assertEquals(1L, affectedAreaList.get(0).getId());
    assertEquals(10.77, affectedAreaList.get(0).getLongitude());
    assertEquals(59.92, affectedAreaList.get(0).getLatitude());
    assertEquals(1.0, affectedAreaList.get(0).getHighDangerRadiusKm());
    assertEquals(2.0, affectedAreaList.get(0).getMediumDangerRadiusKm());
    assertEquals(3.0, affectedAreaList.get(0).getLowDangerRadiusKm());
    assertEquals(3, affectedAreaList.get(0).getSeverityLevel());
    assertEquals("Evacuate immediately due to chemical spill.", affectedAreaList.get(0).getDescription());
    assertEquals("2023-10-01T12:00", affectedAreaList.get(0).getStartDate().toString());
  }

  @Test
  @Rollback
  void addAffectedArea_shouldInsertNewAreaAndAssignId() {
    affectedAreaRepo.addAffectedArea(testArea1);

    assertNotNull(testArea1.getId(), "ID should be assigned after insertion");
    assertTrue(testArea1.getId() > 0, "ID should be greater than 0");

  }

  @Test
  @Rollback
  void deleteAffectedArea_shouldRemoveAreaFromDatabase() {
    affectedAreaRepo.addAffectedArea(testArea2);
    assertNotNull(testArea2.getId());

    int rowsAffected = affectedAreaRepo.deleteAffectedArea(testArea2.getId());
    assertEquals(1, rowsAffected, "One row should be deleted");

    List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM affected_areas WHERE id = ?", testArea2.getId());
    assertTrue(results.isEmpty(), "The deleted area should not exist");
  }

  @Test
  @Rollback
  void updateAffectedArea_shouldUpdateExistingRecord() {
    affectedAreaRepo.addAffectedArea(testArea1);
    assertNotNull(testArea1.getId());

    AffectedArea updatedArea = new AffectedArea();
    updatedArea.setId(testArea1.getId());
    updatedArea.setName("Name");
    updatedArea.setLongitude(12.5);
    updatedArea.setLatitude(64.5);
    updatedArea.setHighDangerRadiusKm(7.0);
    updatedArea.setMediumDangerRadiusKm(12.0);
    updatedArea.setLowDangerRadiusKm(17.0);
    updatedArea.setSeverityLevel(2);
    updatedArea.setDescription("Updated Area Description");
    updatedArea.setStartDate(LocalDateTime.now());

    int rowsAffected = affectedAreaRepo.updateAffectedArea(updatedArea);
    assertEquals(1, rowsAffected, "One row should be updated");

    List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM affected_areas WHERE id = ?", updatedArea.getId());
    assertEquals(1, results.size());
    Map<String, Object> retrievedArea = results.get(0);
    assertEquals(updatedArea.getName(), retrievedArea.get("name"));
    assertEquals(updatedArea.getLongitude(), (Double) retrievedArea.get("longitude"), 0.001);
    assertEquals(updatedArea.getLatitude(), (Double) retrievedArea.get("latitude"), 0.001);
    assertEquals(updatedArea.getHighDangerRadiusKm(), (Double) retrievedArea.get("high_danger_radius_km"), 0.001);
    assertEquals(updatedArea.getMediumDangerRadiusKm(), (Double) retrievedArea.get("medium_danger_radius_km"), 0.001);
    assertEquals(updatedArea.getLowDangerRadiusKm(), (Double) retrievedArea.get("low_danger_radius_km"), 0.001);
    assertEquals(updatedArea.getSeverityLevel(), (Integer) retrievedArea.get("severity_level"));
    assertEquals(updatedArea.getDescription(), retrievedArea.get("description"));
    assertEquals(java.sql.Timestamp.valueOf(updatedArea.getStartDate()).toLocalDateTime().truncatedTo(ChronoUnit.MILLIS),
            ((java.sql.Timestamp) retrievedArea.get("start_time")).toLocalDateTime().truncatedTo(ChronoUnit.MILLIS));
  }
}
