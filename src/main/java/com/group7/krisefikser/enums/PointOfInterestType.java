package com.group7.krisefikser.enums;

import java.util.Arrays;
import lombok.Getter;

/**
 * Enum representing different types of points of interest.
 * Each type has a corresponding string representation.
 */
@Getter
public enum PointOfInterestType {
  SHELTER("shelter"),
  FOOD_CENTRAL("food_central"),
  WATER_STATION("water_station"),
  DEFIBRILLATOR("defibrillator"),
  HOSPITAL("hospital");

  private final String type;

  /**
   * Constructor for PointOfInterestType enum.
   *
   * @param type The string representation of the point of interest type.
   */
  PointOfInterestType(String type) {
    this.type = type;
  }

  /**
   * Method to convert a string to a PointOfInterestType enum.
   * This method will throw an IllegalArgumentException if the string does not match any
   * enum type.
   *
   * @param type The string representation of the point of interest type.
   * @return The corresponding PointOfInterestType enum.
   */
  public static PointOfInterestType fromString(String type) {
    return Arrays.stream(PointOfInterestType.values())
            .filter(poiType -> poiType.getType().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid type: " + type));
  }
}
