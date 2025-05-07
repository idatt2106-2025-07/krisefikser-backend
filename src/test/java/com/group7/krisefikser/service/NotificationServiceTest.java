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
  void getIncidentsNotification_shouldReturnOnlySeverityLevelThree() {
    AffectedAreaResponse highSeverity = new AffectedAreaResponse();
    highSeverity.setDescription("Flood warning");
    highSeverity.setSeverityLevel(3);

    AffectedAreaResponse lowSeverity = new AffectedAreaResponse();
    lowSeverity.setDescription("Minor roadblock");
    lowSeverity.setSeverityLevel(1);

    when(affectedAreaService.getAllAffectedAreas())
        .thenReturn(Arrays.asList(highSeverity, lowSeverity));

    List<NotificationResponse> result = notificationService.getIncidentsNotification();

    assertEquals(1, result.size());
    assertEquals("Flood warning", result.get(0).getMessage());
  }

  @Test
  void getIncidentsNotification_shouldReturnEmptyListWhenNoHighSeverity() {
    AffectedAreaResponse lowSeverity = new AffectedAreaResponse();
    lowSeverity.setDescription("Light rain");
    lowSeverity.setSeverityLevel(1);

    when(affectedAreaService.getAllAffectedAreas())
        .thenReturn(Collections.singletonList(lowSeverity));

    List<NotificationResponse> result = notificationService.getIncidentsNotification();

    assertTrue(result.isEmpty());
  }

  @Test
  void getIncidentsNotification_shouldReturnEmptyListWhenNoAreas() {
    when(affectedAreaService.getAllAffectedAreas()).thenReturn(Collections.emptyList());

    List<NotificationResponse> result = notificationService.getIncidentsNotification();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}