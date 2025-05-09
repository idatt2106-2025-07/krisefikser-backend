package com.group7.krisefikser.dto.response.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a dto response for an affected area with its geographical coordinates,
 * danger radius and notification message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffectedAreaResponse {
  private Long id;
  private String name;
  private Double longitude;
  private Double latitude;
  private Double highDangerRadiusKm;
  private Double mediumDangerRadiusKm;
  private Double lowDangerRadiusKm;
  private int severityLevel;
  private String description;
  private String startDate;
}
