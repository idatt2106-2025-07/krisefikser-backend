package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.location.AffectedAreaRequest;
import com.group7.krisefikser.dto.response.location.AffectedAreaResponse;
import com.group7.krisefikser.mapper.location.AffectedAreaMapper;
import com.group7.krisefikser.model.location.AffectedArea;
import com.group7.krisefikser.repository.location.AffectedAreaRepo;
import com.group7.krisefikser.service.location.AffectedAreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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

  private AffectedAreaRequest testAreaRequest1;
  private AffectedAreaRequest testAreaRequest2;
  private AffectedArea testArea1;
  @BeforeEach
  void setUp() {
    testAreaRequest1 = new AffectedAreaRequest();
    testAreaRequest1.setLongitude(11.5);
    testAreaRequest1.setLatitude(63.5);
    testAreaRequest1.setHighDangerRadiusKm(6.0);
    testAreaRequest1.setMediumDangerRadiusKm(11.0);
    testAreaRequest1.setLowDangerRadiusKm(16.0);
    testAreaRequest1.setSeverityLevel(2);
    testAreaRequest1.setDescription("Test Area 1");
    testAreaRequest1.setStartDate(LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_DATE_TIME));

    testAreaRequest2 = new AffectedAreaRequest();
    testAreaRequest2.setLongitude(12.0);
    testAreaRequest2.setLatitude(64.0);
    testAreaRequest2.setHighDangerRadiusKm(7.0);
    testAreaRequest2.setMediumDangerRadiusKm(12.0);
    testAreaRequest2.setLowDangerRadiusKm(17.0);
    testAreaRequest2.setSeverityLevel(1);
    testAreaRequest2.setDescription("Test Area 2");
    testAreaRequest2.setStartDate(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME));

    testArea1 = AffectedAreaMapper.INSTANCE.requestToAffectedArea(testAreaRequest1);
  }

  /**
   * Test for getAllAffectedAreas method.
   * This test verifies that the method returns the correct response
   * when called.
   */
  @Test
  void getAllAffectedAreas_shouldReturnListOfAffectedAreaResponses() {
    List<AffectedArea> affectedAreas = Arrays.asList(
            new AffectedArea(1L, "Name 1", 10.0, 60.0, 5.0, 6.1, 7.0,
                    1, "High danger area 1", LocalDateTime.now()),
            new AffectedArea(2L, "Name 2", 11.0, 61.0, 3.0, 4.1, 4.9,
                    2, "Medium danger area 2", LocalDateTime.now())
    );
    when(affectedAreaRepo.getAllAffectedAreas()).thenReturn(affectedAreas);

    List<AffectedAreaResponse> responses = affectedAreaService.getAllAffectedAreas();

    assertEquals(2, responses.size());

    AffectedAreaResponse response1 = responses.get(0);
    assertEquals(1L, response1.getId());
    assertEquals("Name 1", response1.getName());
    assertEquals(10.0, response1.getLongitude());
    assertEquals(60.0, response1.getLatitude());
    assertEquals(5.0, response1.getHighDangerRadiusKm());
    assertEquals(6.1, response1.getMediumDangerRadiusKm());
    assertEquals(7.0, response1.getLowDangerRadiusKm());
    assertEquals(1.0, response1.getSeverityLevel());
    assertEquals("High danger area 1", response1.getDescription());
    assertEquals(affectedAreas.get(0).getStartDate().format(DateTimeFormatter.ISO_DATE_TIME),
            response1.getStartDate());

    AffectedAreaResponse response2 = responses.get(1);
    assertEquals(2L, response2.getId());
    assertEquals("Name 2", response2.getName());
    assertEquals(11.0, response2.getLongitude());
    assertEquals(61.0, response2.getLatitude());
    assertEquals(3.0, response2.getHighDangerRadiusKm());
    assertEquals(4.1, response2.getMediumDangerRadiusKm());
    assertEquals(4.9, response2.getLowDangerRadiusKm());
    assertEquals(2, response2.getSeverityLevel());
    assertEquals("Medium danger area 2", response2.getDescription());
    assertEquals(affectedAreas.get(1).getStartDate().format(DateTimeFormatter.ISO_DATE_TIME),
            response2.getStartDate());
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

  @Test
  void addAffectedArea_successfulAddition() {
    doAnswer(invocation -> {
      AffectedArea area = invocation.getArgument(0);
      area.setId(1L);
      return null;
    }).when(affectedAreaRepo).addAffectedArea(any(AffectedArea.class));

    AffectedAreaResponse response = affectedAreaService.addAffectedArea(testAreaRequest1);

    assertNotNull(response);
    assertEquals(testAreaRequest1.getLongitude(), response.getLongitude());
    assertEquals(testAreaRequest1.getLatitude(), response.getLatitude());
    assertEquals(testAreaRequest1.getHighDangerRadiusKm(), response.getHighDangerRadiusKm());
    assertEquals(testAreaRequest1.getMediumDangerRadiusKm(), response.getMediumDangerRadiusKm());
    assertEquals(testAreaRequest1.getLowDangerRadiusKm(), response.getLowDangerRadiusKm());
    assertEquals(testAreaRequest1.getSeverityLevel(), response.getSeverityLevel());
    assertEquals(testAreaRequest1.getDescription(), response.getDescription());
    assertEquals(testAreaRequest1.getStartDate(), response.getStartDate());
    assertNotNull(response.getId());
    verify(affectedAreaRepo, times(1)).addAffectedArea(any(AffectedArea.class));
  }

  @Test
  void addAffectedArea_failedAddition() {
    doAnswer(invocation -> {
      AffectedArea area = invocation.getArgument(0);
      area.setId(null);
      return null;
    }).when(affectedAreaRepo).addAffectedArea(any(AffectedArea.class));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      affectedAreaService.addAffectedArea(testAreaRequest1);
    });

    assertEquals("Failed to add affected area", exception.getMessage());
    verify(affectedAreaRepo, times(1)).addAffectedArea(any(AffectedArea.class));
  }

  @Test
  void deleteAffectedArea_successfulDeletion() {
    long areaIdToDelete = 1L;
    when(affectedAreaRepo.deleteAffectedArea(areaIdToDelete)).thenReturn(1);

    assertDoesNotThrow(() -> affectedAreaService.deleteAffectedArea(areaIdToDelete));
    verify(affectedAreaRepo, times(1)).deleteAffectedArea(areaIdToDelete);
  }

  @Test
  void deleteAffectedArea_failedDeletion_noRowsAffected() {
    long areaIdToDelete = 1L;
    when(affectedAreaRepo.deleteAffectedArea(areaIdToDelete)).thenReturn(0);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      affectedAreaService.deleteAffectedArea(areaIdToDelete);
    });

    assertEquals("Failed to delete affected area", exception.getMessage());
    verify(affectedAreaRepo, times(1)).deleteAffectedArea(areaIdToDelete);
  }

  @Test
  void deleteAffectedArea_failedDeletion_multipleRowsAffected() {
    long areaIdToDelete = 1L;
    when(affectedAreaRepo.deleteAffectedArea(areaIdToDelete)).thenReturn(2);

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      affectedAreaService.deleteAffectedArea(areaIdToDelete);
    });

    assertEquals("Multiple rows deleted, check database integrity", exception.getMessage());
    verify(affectedAreaRepo, times(1)).deleteAffectedArea(areaIdToDelete);
  }

  @Test
  void updateAffectedArea_successfulUpdate() {
    long areaIdToUpdate = 1L;
    testArea1.setId(areaIdToUpdate);
    when(affectedAreaRepo.updateAffectedArea(any(AffectedArea.class))).thenReturn(1);

    AffectedAreaResponse response = affectedAreaService.updateAffectedArea(areaIdToUpdate, testAreaRequest1);

    assertNotNull(response);
    assertEquals(areaIdToUpdate, response.getId());
    assertEquals(testAreaRequest1.getLongitude(), response.getLongitude());
    assertEquals(testAreaRequest1.getLatitude(), response.getLatitude());
    assertEquals(testAreaRequest1.getHighDangerRadiusKm(), response.getHighDangerRadiusKm());
    assertEquals(testAreaRequest1.getMediumDangerRadiusKm(), response.getMediumDangerRadiusKm());
    assertEquals(testAreaRequest1.getLowDangerRadiusKm(), response.getLowDangerRadiusKm());
    assertEquals(testAreaRequest1.getSeverityLevel(), response.getSeverityLevel());
    assertEquals(testAreaRequest1.getDescription(), response.getDescription());
    assertEquals(testAreaRequest1.getStartDate(), response.getStartDate());
    verify(affectedAreaRepo, times(1)).updateAffectedArea(argThat(area -> area.getId().equals(areaIdToUpdate)));
  }

  @Test
  void updateAffectedArea_failedUpdate() {
    long areaIdToUpdate = 1L;
    when(affectedAreaRepo.updateAffectedArea(any(AffectedArea.class))).thenReturn(0);

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      affectedAreaService.updateAffectedArea(areaIdToUpdate, testAreaRequest1);
    });

    assertEquals("Failed to update affected area", exception.getMessage());
    verify(affectedAreaRepo, times(1)).updateAffectedArea(argThat(area -> area.getId().equals(areaIdToUpdate)));
  }
}

