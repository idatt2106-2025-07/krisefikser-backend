package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.request.PointOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.PointOfInterest;
import com.group7.krisefikser.repository.PointOfInterestRepo;
import com.group7.krisefikser.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PointOfInterestService class.
 * This class tests the methods of the PointOfInterestService class
 * to ensure they behave as expected.
 */
@ExtendWith(MockitoExtension.class)
class PointOfInterestServiceTest {
  @Mock
  private PointOfInterestRepo pointOfInterestRepo;
  @Mock
  private JwtUtils jwtUtils;
  @InjectMocks
  private PointOfInterestService pointOfInterestService;

  private PointOfInterestRequest addRequest;
  private PointOfInterest savedPoint;
  private PointOfInterestRequest updateRequest;
  private String validToken;
  private String invalidToken;

  @BeforeEach
  void setUp() {
    validToken = "validTestToken";
    invalidToken = "invalidTestToken";

    addRequest = new PointOfInterestRequest(
            10.0, 20.0, "shelter", "09:00", "17:00", "12345678", "A safe shelter"
    );
    savedPoint = new PointOfInterest(
            1L, 10.0, 20.0, PointOfInterestType.SHELTER, LocalTime.parse("09:00"), LocalTime.parse("17:00"), "12345678", "A safe shelter"
    );
    updateRequest = new PointOfInterestRequest(
            11.0, 21.0, "hospital", "10:00", "18:00", "87654321", "Updated hospital info"
    );
  }

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
            new PointOfInterest(1L, 63.4297, 10.3933, PointOfInterestType.SHELTER,
                    null, null, "12345678", "Shelter description"),
            new PointOfInterest(2L, 63.4300, 10.4000, PointOfInterestType.WATER_STATION,
                    null, null, "12345678", "Water station description")
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

  @Test
  void addPointOfInterest_authorizedUser_shouldAddAndReturnResponse() throws IllegalAccessException, JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");
    doAnswer(invocation -> {
      PointOfInterest argument = invocation.getArgument(0);
      argument.setId(1L);
      return null;
    }).when(pointOfInterestRepo).addPointOfInterest(any(PointOfInterest.class));

    PointOfInterestResponse response = pointOfInterestService.addPointOfInterest(validToken, addRequest);

    assertNotNull(response);
    assertEquals(savedPoint.getId(), response.getId());
    assertEquals(addRequest.getLatitude(), response.getLatitude());
    assertEquals(addRequest.getLongitude(), response.getLongitude());
    assertEquals(addRequest.getType().toUpperCase(), response.getType());
    assertEquals(addRequest.getOpensAt(), response.getOpensAt());
    assertEquals(addRequest.getClosesAt(), response.getClosesAt());
    assertEquals(addRequest.getContactNumber(), response.getContactNumber());
    assertEquals(addRequest.getDescription(), response.getDescription());
    verify(pointOfInterestRepo, times(1)).addPointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void addPointOfInterest_unauthorizedUser_shouldThrowIllegalAccessException() throws JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(invalidToken)).thenReturn("normal");

    assertThrows(IllegalAccessException.class, () -> pointOfInterestService.addPointOfInterest(invalidToken, addRequest));
    verify(pointOfInterestRepo, never()).addPointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void addPointOfInterest_jwtException_shouldThrowIllegalAccessException() throws JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(invalidToken)).thenThrow(JwtMissingPropertyException.class);

    assertThrows(IllegalAccessException.class, () -> pointOfInterestService.addPointOfInterest(invalidToken, addRequest));
    verify(pointOfInterestRepo, never()).addPointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void addPointOfInterest_repoFailsToAdd_shouldThrowIllegalStateException() throws JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");
    doAnswer(invocation -> null).when(pointOfInterestRepo)
            .addPointOfInterest(any(PointOfInterest.class));

    assertThrows(IllegalStateException.class, () -> pointOfInterestService.addPointOfInterest(validToken, addRequest));
    verify(pointOfInterestRepo, times(1)).addPointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void deletePointOfInterest_existingId_shouldCallRepoOnce() throws JwtMissingPropertyException, IllegalAccessException {
    long idToDelete = 1L;
    when(pointOfInterestRepo.deletePointOfInterest(idToDelete)).thenReturn(1);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");

    pointOfInterestService.deletePointOfInterest(validToken, idToDelete);

    verify(pointOfInterestRepo, times(1)).deletePointOfInterest(idToDelete);
  }

  @Test
  void deletePointOfInterest_nonExistingId_shouldThrowIllegalArgumentException() throws JwtMissingPropertyException {
    long idToDelete = 99L;
    when(pointOfInterestRepo.deletePointOfInterest(idToDelete)).thenReturn(0);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");

    assertThrows(IllegalArgumentException.class, () -> pointOfInterestService
            .deletePointOfInterest(validToken, idToDelete));
    verify(pointOfInterestRepo, times(1)).deletePointOfInterest(idToDelete);
  }

  @Test
  void deletePointOfInterest_multipleRowsDeleted_shouldThrowIllegalStateException() throws JwtMissingPropertyException {
    long idToDelete = 1L;
    when(pointOfInterestRepo.deletePointOfInterest(idToDelete)).thenReturn(2);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");

    assertThrows(IllegalStateException.class, () -> pointOfInterestService
            .deletePointOfInterest(validToken, idToDelete));
    verify(pointOfInterestRepo, times(1))
            .deletePointOfInterest(idToDelete);
  }
  @Test
  void deletePointOfInterest_unauthorizedUser_shouldThrowIllegalAccessException() throws JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(invalidToken)).thenReturn("normal");

    assertThrows(IllegalAccessException.class, () -> pointOfInterestService.deletePointOfInterest(invalidToken, 1L));
    verify(pointOfInterestRepo, never()).addPointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void updatePointOfInterest_existingId_shouldUpdateAndReturnResponse() throws IllegalAccessException, JwtMissingPropertyException {
    when(pointOfInterestRepo.updatePointOfInterest(any(PointOfInterest.class)))
            .thenReturn(1);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");


    PointOfInterestResponse response = pointOfInterestService
            .updatePointOfInterest(1L, validToken, updateRequest);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(updateRequest.getLatitude(), response.getLatitude());
    assertEquals(updateRequest.getLongitude(), response.getLongitude());
    assertEquals(updateRequest.getType().toUpperCase(), response.getType());
    assertEquals(updateRequest.getOpensAt(), response.getOpensAt());
    assertEquals(updateRequest.getClosesAt(), response.getClosesAt());
    assertEquals(updateRequest.getContactNumber(), response.getContactNumber());
    assertEquals(updateRequest.getDescription(), response.getDescription());
    verify(pointOfInterestRepo, times(1))
            .updatePointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void updatePointOfInterest_nonExistingId_shouldThrowIllegalArgumentException() throws JwtMissingPropertyException {
    PointOfInterestRequest nonExistingRequest = new PointOfInterestRequest(
            11.0, 21.0, "hospital", "10:00", "18:00", "87654321", "Updated hospital info"
    );
    when(pointOfInterestRepo.updatePointOfInterest(any(PointOfInterest.class)))
            .thenReturn(0);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");

    assertThrows(IllegalArgumentException.class, () -> pointOfInterestService
            .updatePointOfInterest(99L, validToken, nonExistingRequest));
    verify(pointOfInterestRepo, times(1))
            .updatePointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void updatePointOfInterest_multipleRowsUpdated_shouldThrowIllegalStateException() throws JwtMissingPropertyException {
    when(pointOfInterestRepo.updatePointOfInterest(any(PointOfInterest.class))).thenReturn(2);
    when(jwtUtils.validateTokenAndGetRole(validToken)).thenReturn("admin");

    assertThrows(IllegalStateException.class, () -> pointOfInterestService
            .updatePointOfInterest(1L, validToken, updateRequest));
    verify(pointOfInterestRepo, times(1)).updatePointOfInterest(any(PointOfInterest.class));
  }

  @Test
  void updatePointOfInterest_unauthorizedUser_shouldThrowIllegalAccessException() throws JwtMissingPropertyException {
    when(jwtUtils.validateTokenAndGetRole(invalidToken)).thenReturn("normal");

    assertThrows(IllegalAccessException.class, () -> pointOfInterestService
            .updatePointOfInterest(1L, invalidToken, addRequest));
    verify(pointOfInterestRepo, never()).addPointOfInterest(any(PointOfInterest.class));
  }
}
