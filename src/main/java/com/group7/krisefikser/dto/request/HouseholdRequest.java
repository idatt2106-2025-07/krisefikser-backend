package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Household entity with validation constraints.
 * Used for creating and updating household information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdRequest {

  @NotBlank(message = "Household name is required")
  @Size(min = 2, max = 100, message = "Household name must be between 2 and 100 characters")
  private String name;

  @NotNull(message = "Longitude is required")
  @Min(value = -180, message = "Longitude must be greater than or equal to -180")
  @Max(value = 180, message = "Longitude must be less than or equal to 180")
  private Double longitude;

  @NotNull(message = "Latitude is required")
  @Min(value = -90, message = "Latitude must be greater than or equal to -90")
  @Max(value = 90, message = "Latitude must be less than or equal to 90")
  private Double latitude;
}