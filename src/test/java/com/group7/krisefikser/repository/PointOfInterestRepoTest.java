package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.PointOfInterest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class is a test class for the PointOfInterestRepo.
 * It tests the functionality of the methods in the PointOfInterestRepo class.
 */
@SpringBootTest
@ActiveProfiles("test")
class PointOfInterestRepoTest {
  @Autowired
  private PointOfInterestRepo pointOfInterestRepo;

  /**
   * This method tests the getAllPointsOfInterest method in the PointOfInterestRepo class.
   * It retrieves all points of interest from the database and checks if the list is not null,
   * the size of the list is correct, and the attributes of the first point of interest are correct.
   */
  @Test
  void getAllPointsOfInterest() {
    List<PointOfInterest> pointsOfInterest = pointOfInterestRepo.getAllPointsOfInterest();

    assertNotNull(pointsOfInterest);
    assertEquals(5, pointsOfInterest.size());
    assertEquals(1, pointsOfInterest.get(0).getId());
    assertEquals(10.76, pointsOfInterest.get(0).getLongitude());
    assertEquals(59.91, pointsOfInterest.get(0).getLatitude());
    assertEquals("SHELTER", pointsOfInterest.get(0).getType().name());
  }
}
