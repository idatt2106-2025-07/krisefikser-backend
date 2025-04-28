package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.model.PointOfInterest;
import com.group7.krisefikser.repository.PointOfInterestRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PointOfInterestService class.
 * This class tests the methods of the PointOfInterestService class
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class PointOfInterestServiceTest {
  @Mock
  private PointOfInterestRepo pointOfInterestRepo;

  @InjectMocks
  private PointOfInterestService pointOfInterestService;

  /**
   * Test for getPointsOfInterestByTypes method.
   * This test verifies that the method returns empty list then the request types is null.
   */
  @Test
  void getPointsOfInterestByTypes_shouldReturnEmptyList_whenRequestTypesIsNull() {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(null);
    List<PointOfInterestResponse> response = pointOfInterestService.getPointsOfInterestByTypes(request);
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  /**
   * Test for getPointsOfInterestByTypes method.
   * This test verifies that the method returns empty list then the request types is empty.
   */
  @Test
  void getPointsOfInterestByTypes_shouldReturnEmptyList_whenRequestTypesIsEmpty() {
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(Collections.emptyList());
    List<PointOfInterestResponse> response = pointOfInterestService.getPointsOfInterestByTypes(request);
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  /**
   * Test for getPointsOfInterestByTypes method.
   * This test verifies that the method returns points of interest
   * when valid types are provided in the request.
   */
  @Test
  void getPointsOfInterestByTypes_shouldReturnPoints_forValidTypes() {
    List<String> requestedTypes = Arrays.asList("SHELTER", "WATER_STATION");
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(requestedTypes);

    List<PointOfInterest> mockPoints = Arrays.asList(
            new PointOfInterest(1L, 63.4297, 10.3933, PointOfInterestType.SHELTER),
            new PointOfInterest(2L, 63.4300, 10.4000, PointOfInterestType.WATER_STATION)
    );
    when(pointOfInterestRepo.getPointsOfInterestByTypes(
            requestedTypes.stream().map(PointOfInterestType::fromString).toList())
    ).thenReturn(mockPoints);

    List<PointOfInterestResponse> response = pointOfInterestService.getPointsOfInterestByTypes(request);

    assertNotNull(response);
    assertEquals(2, response.size());
    assertEquals(1L, response.get(0).getId());
    assertEquals(63.4297, response.get(0).getLatitude());
    assertEquals(10.3933, response.get(0).getLongitude());
    assertEquals("SHELTER", response.get(0).getType());
    assertEquals(2L, response.get(1).getId());
    assertEquals(63.4300, response.get(1).getLatitude());
    assertEquals(10.4000, response.get(1).getLongitude());
    assertEquals("WATER_STATION", response.get(1).getType());
  }

  /**
   * Test for getPointsOfInterestByTypes method.
   * This test verifies that the method throws an exception
   * when an invalid type is provided in the request.
   */
  @Test
  void getPointsOfInterestByTypes_shouldThrowIllegalArgumentException_forInvalidType() {
    List<String> requestedTypes = Collections.singletonList("invalid_type");
    GetPointsOfInterestRequest request = new GetPointsOfInterestRequest(requestedTypes);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            pointOfInterestService.getPointsOfInterestByTypes(request));

    assertEquals("Invalid point of interest type provided", exception.getMessage());
    assertNotNull(exception.getCause());
    assertEquals("Invalid type: invalid_type", exception.getCause().getMessage());
  }
}
