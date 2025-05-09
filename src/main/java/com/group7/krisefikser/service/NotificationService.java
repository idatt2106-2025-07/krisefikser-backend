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
  private static final double EARTH_RADIUS_KM = 6371.0;

  /**
   * Retrieves a list of incident notifications.
   *
   * @return a list of NotificationResponse objects containing details of incidents.
   */
  public List<NotificationResponse> getIncidentsNotification(double lat, double lon) {
    List<NotificationResponse> incidents = new ArrayList<>();
    for (AffectedAreaResponse area : affectedAreaService.getAllAffectedAreas()) {
      if (calculateDistance(area.getLatitude(), area.getLongitude(), lat, lon)
          <= area.getMediumDangerRadiusKm()) {
        incidents.add(new NotificationResponse(area.getDescription()));
      }
    }
    return incidents;
  }

  /**
   * Calculates the distance between two geographical points using the Haversine formula.
   * This method is used to determine the distance between two points on the Earth's surface
   * given their latitude and longitude.
   *
   * @param lat1 the latitude of the first point
   * @param lon1 the longitude of the first point
   * @param lat2 the latitude of the second point
   * @param lon2 the longitude of the second point
   * @return the distance in kilometers between the two points
   */
  public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_KM * c;
  }
}
