package com.group7.krisefikser.dto.response;

import lombok.Data;

/**
 * Represents a response containing information about a household.
 * This class contains the ID, name, longitude, and latitude of the household.
 * It is used to transfer data related to household responses.
 */
@Data
public class HouseholdResponse {
  private Long id;
  private String name;
  private Double longitude;
  private Double latitude;
}