package com.group7.krisefikser.dto.request;

import lombok.Data;

/**
 * This class represents a request for notifications.
 * It contains the latitude and longitude of the user's location.
 */
@Data
public class NotificationRequest {
  private double latitude;
  private double longitude;
}
