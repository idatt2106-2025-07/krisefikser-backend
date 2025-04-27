package com.group7.krisefikser.enums;

import java.util.Arrays;

/**
 * Enum representing different types of points of interest.
 * Each type has a corresponding string representation.
 */
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
   * @param type
   */
  PointOfInterestType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static PointOfInterestType fromString(String type) {
    return Arrays.stream(PointOfInterestType.values())
            .filter(poiType -> poiType.getType().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid type: " + type));
  }
}
