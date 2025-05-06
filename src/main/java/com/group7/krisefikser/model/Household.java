package com.group7.krisefikser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a household entity with basic details such as name, location, ID
 * and emergency group id.
 * This class is used to model a household in the system.
 * Annotations:
 * - @Data: Generates getters, setters, toString, equals, and hashCode methods.
 * - @AllArgsConstructor: Generates a constructor with all fields as parameters.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Household {
  private Long id;
  private String name;
  private Double longitude;
  private Double latitude;
  private Long emergencyGroupId;
}