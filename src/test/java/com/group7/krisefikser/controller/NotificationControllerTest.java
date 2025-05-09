package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.NotificationRequest;
import com.group7.krisefikser.dto.response.NotificationResponse;
import com.group7.krisefikser.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NotificationService notificationService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("POST /api/notification/incidents - Success within danger zone")
  void testGetIncidentsNotificationsSuccess() throws Exception {
    NotificationRequest request = new NotificationRequest();
    request.setLatitude(60.0);
    request.setLongitude(10.85);

    List<NotificationResponse> mockNotifications = Arrays.asList(
        new NotificationResponse("High water level detected"),
        new NotificationResponse("Severe drought increasing fire risk")
    );

    when(notificationService.withinDangerZone(60.0, 10.85)).thenReturn(true);
    when(notificationService.getIncidentsNotification()).thenReturn(mockNotifications);

    mockMvc.perform(post("/api/notification/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size()").value(mockNotifications.size()))
        .andExpect(jsonPath("$[0].message").value("High water level detected"))
        .andExpect(jsonPath("$[1].message").value("Severe drought increasing fire risk"));
  }

  @Test
  @DisplayName("POST /api/notification/incidents - Success outside danger zone (empty list)")
  void testGetIncidentsNotificationsOutsideDangerZone() throws Exception {
    NotificationRequest request = new NotificationRequest();
    request.setLatitude(60.0);
    request.setLongitude(10.85);

    when(notificationService.withinDangerZone(60.0, 10.85)).thenReturn(false);

    mockMvc.perform(post("/api/notification/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  @DisplayName("POST /api/notification/incidents - Internal Server Error")
  void testGetIncidentsNotificationsFailure() throws Exception {
    NotificationRequest request = new NotificationRequest();
    request.setLatitude(60.0);
    request.setLongitude(10.85);

    when(notificationService.withinDangerZone(60.0, 10.85))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(post("/api/notification/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }
}