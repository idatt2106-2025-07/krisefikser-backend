package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.model.AffectedArea;
import com.group7.krisefikser.repository.AffectedAreaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AffectedAreaService class.
 * This class tests the methods of the AffectedAreaService class
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class AffectedAreaServiceTest {
  @Mock
  private AffectedAreaRepo affectedAreaRepo;
  @InjectMocks
  private AffectedAreaService affectedAreaService;

  /**
   * Test for getAllAffectedAreas method.
   * This test verifies that the method returns the correct response
   * when called.
   */
  @Test
  void getAllAffectedAreas_shouldReturnListOfAffectedAreaResponses() {
    List<AffectedArea> affectedAreas = Arrays.asList(
            new AffectedArea(1L, 10.0, 60.0, 5.0,
                    10.0, 15.0, "High danger area 1"),
            new AffectedArea(2L, 11.0, 61.0, 3.0,
                    7.0, 12.0, "Medium danger area 2")
    );
    when(affectedAreaRepo.getAllAffectedAreas()).thenReturn(affectedAreas);

    List<AffectedAreaResponse> responses = affectedAreaService.getAllAffectedAreas();

    assertEquals(2, responses.size());

    AffectedAreaResponse response1 = responses.get(0);
    assertEquals(1L, response1.getId());
    assertEquals(10.0, response1.getLongitude());
    assertEquals(60.0, response1.getLatitude());
    assertEquals(5.0, response1.getHighDangerRadiusKm());
    assertEquals(10.0, response1.getMediumDangerRadiusKm());
    assertEquals(15.0, response1.getLowDangerRadiusKm());
    assertEquals("High danger area 1", response1.getNotificationMessage());

    AffectedAreaResponse response2 = responses.get(1);
    assertEquals(2L, response2.getId());
    assertEquals(11.0, response2.getLongitude());
    assertEquals(61.0, response2.getLatitude());
    assertEquals(3.0, response2.getHighDangerRadiusKm());
    assertEquals(7.0, response2.getMediumDangerRadiusKm());
    assertEquals(12.0, response2.getLowDangerRadiusKm());
    assertEquals("Medium danger area 2", response2.getNotificationMessage());
  }

  /**
   * Test for getAllAffectedAreas method.
   * This test verifies that the method returns an empty list
   * when no affected areas exist in the repository.
   */
  @Test
  void getAllAffectedAreas_shouldReturnEmptyList_whenNoAffectedAreasExist() {
    when(affectedAreaRepo.getAllAffectedAreas()).thenReturn(List.of());

    List<AffectedAreaResponse> responses = affectedAreaService.getAllAffectedAreas();

    assertEquals(0, responses.size());
  }
}

