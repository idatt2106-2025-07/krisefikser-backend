package com.group7.krisefikser.dto.response.location;

import lombok.Data;

/**
 * DTO for household member position response.
 * Contains fields for latitude, longitude, and name.
 */
@Data
public class HouseholdMemberPositionResponse {
  private double latitude;
  private double longitude;
  private String name;
}
