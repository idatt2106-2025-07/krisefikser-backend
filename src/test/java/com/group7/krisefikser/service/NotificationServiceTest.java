package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.dto.response.NotificationResponse;
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

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private AffectedAreaService affectedAreaService;

  @InjectMocks
  private NotificationService notificationService;

  @Test
  void getIncidentsNotification_shouldReturnMatchingAreaWithinRadius() {
    AffectedAreaResponse matchingArea = new AffectedAreaResponse();
    matchingArea.setDescription("Flood warning");
    matchingArea.setLatitude(60.0);
    matchingArea.setLongitude(10.85);
    matchingArea.setMediumDangerRadiusKm(5.0);  // within radius

    AffectedAreaResponse outsideArea = new AffectedAreaResponse();
    outsideArea.setDescription("Landslide");
    outsideArea.setLatitude(61.0); // ~111 km north
    outsideArea.setLongitude(10.85);
    outsideArea.setMediumDangerRadiusKm(5.0);

    when(affectedAreaService.getAllAffectedAreas())
        .thenReturn(Arrays.asList(matchingArea, outsideArea));

    List<NotificationResponse> result = notificationService.getIncidentsNotification(60.0, 10.85);

    assertEquals(1, result.size());
    assertEquals("Flood warning", result.get(0).getMessage());
  }

  @Test
  void getIncidentsNotification_shouldReturnEmptyListWhenNoAreasWithinRadius() {
    AffectedAreaResponse farAwayArea = new AffectedAreaResponse();
    farAwayArea.setDescription("Distant earthquake");
    farAwayArea.setLatitude(61.0);  // ~111 km away
    farAwayArea.setLongitude(10.85);
    farAwayArea.setMediumDangerRadiusKm(3.0);

    when(affectedAreaService.getAllAffectedAreas())
        .thenReturn(Collections.singletonList(farAwayArea));

    List<NotificationResponse> result = notificationService.getIncidentsNotification(60.0, 10.85);

    assertTrue(result.isEmpty());
  }

  @Test
  void getIncidentsNotification_shouldReturnEmptyListWhenNoAreas() {
    when(affectedAreaService.getAllAffectedAreas()).thenReturn(Collections.emptyList());

    List<NotificationResponse> result = notificationService.getIncidentsNotification(60.0, 10.85);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}