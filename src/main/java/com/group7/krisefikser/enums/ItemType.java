package com.group7.krisefikser.enums;

/**
 * Enum representing different types of items.
 * This enum can be used to categorize items into different types.
 */
public enum ItemType {
    DRINK,
    FOOD,
    ACCESSORIES;

  /**
   * Converts a string to an ItemType enum.
   *
   * @param value the string value to convert
   * @return the corresponding ItemType enum
   * @throws IllegalArgumentException if the value does not match any ItemType
   */
  public static ItemType fromString(String value) {
    if (value == null) {
      return null;
    }

    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown ItemType: " + value);
    }
  }
}