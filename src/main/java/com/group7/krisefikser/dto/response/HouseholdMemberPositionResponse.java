package com.group7.krisefikser.dto.response;

import lombok.Data;

/**
 * DTO for household member position response.
 * Contains fields for latitude, longitude, and name.
 */
@Data
public class HouseholdMemberPositionResponse {
  private String latitude;
  private String longitude;
  private String name;
}
