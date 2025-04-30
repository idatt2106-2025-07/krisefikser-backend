package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.validation.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a response object for a point of interest (POI).
 * It contains the id, latitude, longitude, and type of the POI.
 * It uses Lombok annotations to generate boilerplate code like getters, setters,
 * and constructors.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestRequest {
  @NotNull(message = "Latitude cannot be null")
  private Double latitude;
  @NotNull(message = "Longitude cannot be null")
  private Double longitude;
  @NotNull(message = "Type cannot be null")
  @ValidEnum(enumClass = PointOfInterestType.class,
          message = "Type must be one of the following (case-insensitive): {enumClass}")
  private String type;
  private String opensAt;
  private String closesAt;
  private String contactNumber;
  private String description;
}
