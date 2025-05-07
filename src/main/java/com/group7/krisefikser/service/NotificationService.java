package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.dto.response.NotificationResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling notifications.
 * The role of this service is to manage the business logic related to
 * notifications, such as retrieving incident notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final AffectedAreaService affectedAreaService;

  /**
   * Retrieves a list of incident notifications.
   *
   * @return a list of NotifiationResponse objects containing details of incidents.
   */
  public List<NotificationResponse> getIncidentsNotification() {
    List<NotificationResponse> incidents = new ArrayList<>();
    for (AffectedAreaResponse area : affectedAreaService.getAllAffectedAreas()) {
      if (area.getSeverityLevel() == 3) {
        incidents.add(new NotificationResponse(area.getDescription()));
      }
    }
    return incidents;
  }
}
