package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents a response object for a point of interest (POI).
 * It contains the id, latitude, longitude, and type of the POI.
 * It uses Lombok annotations to generate boilerplate code like getters, setters,
 * and constructors.
 */
@Data
@AllArgsConstructor
public class PointOfInterestResponse {
  private Long id;
  private Double latitude;
  private Double longitude;
  private String type;
}
