package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.AffectedArea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the AffectedAreaRepo.
 * This class is used to test the AffectedAreaRepo functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
class AffectedAreaRepoTest {
  @Autowired
  private AffectedAreaRepo affectedAreaRepo;

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
    assertEquals(3.0, affectedAreaList.get(0).getMediumDangerRadiusKm());
    assertEquals(5.0, affectedAreaList.get(0).getLowDangerRadiusKm());
    assertEquals("Evacuate immediately due to chemical spill.", affectedAreaList.get(0).getNotificationMessage());
  }
}
