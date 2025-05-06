package com.group7.krisefikser.model;

import lombok.Data;

/**
 * Model class representing a user's position.
 * Contains fields for latitude, longitude, name, and userId.
 */
@Data
public class UserPosition {
  private double latitude;
  private double longitude;
  private String name;
  private Long userId;
}
