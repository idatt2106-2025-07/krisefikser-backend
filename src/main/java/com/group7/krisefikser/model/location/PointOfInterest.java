package com.group7.krisefikser.model.location;

import com.group7.krisefikser.enums.PointOfInterestType;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a point of interest (POI) with its attributes.
 * It contains the id, latitude, longitude, and type of the POI.
 * It uses Lombok annotations to generate boilerplate code like getters, setters,
 * and constructors.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterest {
  private Long id;
  private double latitude;
  private double longitude;
  PointOfInterestType type;
  private LocalTime opensAt;
  private LocalTime closesAt;
  private String contactNumber;
  private String description;
}
