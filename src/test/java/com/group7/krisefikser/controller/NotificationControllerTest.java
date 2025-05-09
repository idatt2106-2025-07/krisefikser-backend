package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.other.NotificationRequest;
import com.group7.krisefikser.dto.response.other.NotificationResponse;
import com.group7.krisefikser.service.other.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
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

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/notification/incidents - Success")
  void testPostIncidentsNotificationsSuccess() throws Exception {
    List<NotificationResponse> mockNotifications = Arrays.asList(
        new NotificationResponse("High water level detected"),
        new NotificationResponse("Severe drought increasing fire risk")
    );

    double lat = 60.0;
    double lon = 10.85;

    NotificationRequest request = new NotificationRequest();
    request.setLatitude(lat);
    request.setLongitude(lon);

    when(notificationService.getIncidentsNotification(lat, lon))
        .thenReturn(mockNotifications);

    mockMvc.perform(post("/api/notification/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.size()").value(mockNotifications.size()))
        .andExpect(jsonPath("$[0].message").value("High water level detected"))
        .andExpect(jsonPath("$[1].message").value("Severe drought increasing fire risk"));
  }

  @Test
  @DisplayName("POST /api/notification/incidents - Internal Server Error")
  void testPostIncidentsNotificationsFailure() throws Exception {
    double lat = 60.0;
    double lon = 10.85;

    NotificationRequest request = new NotificationRequest();
    request.setLatitude(lat);
    request.setLongitude(lon);

    when(notificationService.getIncidentsNotification(lat, lon))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(post("/api/notification/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }
}