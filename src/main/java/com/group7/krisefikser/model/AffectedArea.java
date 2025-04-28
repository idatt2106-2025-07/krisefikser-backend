package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an affected area with its geographical coordinates, danger radius
 * levels, and notification message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffectedArea {
  private Long id;
  private Double longitude;
  private Double latitude;
  private Double highDangerRadiusKm;
  private Double mediumDangerRadiusKm;
  private Double lowDangerRadiusKm;
  private String notificationMessage;
}
