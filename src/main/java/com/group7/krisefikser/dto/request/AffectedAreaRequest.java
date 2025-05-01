package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to create or update an affected area with its geographical coordinates,
 * danger radius levels, and notification message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffectedAreaRequest {
  @NotNull(message = "Longitude cannot be null")
  @DecimalMin(value = "-180.0", message = "Longitude must be between -180.0 and 180.0")
  @DecimalMax(value = "180.0", message = "Longitude must be between -180.0 and 180.0")
  private Double longitude;

  @NotNull(message = "Latitude cannot be null")
  @DecimalMin(value = "-90.0", message = "Latitude must be between -90.0 and 90.0")
  @DecimalMax(value = "90.0", message = "Latitude must be between -90.0 and 90.0")
  private Double latitude;

  @NotNull(message = "High danger radius cannot be null")
  @DecimalMin(value = "0.0", message = "High danger radius must be greater than or equal to 0.0")
  private Double highDangerRadiusKm;

  @NotNull(message = "Medium danger radius cannot be null")
  @DecimalMin(value = "0.0", message = "Medium danger radius must be greater than or equal to 0.0")
  private Double mediumDangerRadiusKm;

  @NotNull(message = "Low danger radius cannot be null")
  @DecimalMin(value = "0.0", message = "Low danger radius must be greater than or equal to 0.0")
  private Double lowDangerRadiusKm;

  @NotNull(message = "Severity level cannot be null")
  @Min(value = 1, message = "Severity level must be greater than or equal to 1")
  @Max(value = 3, message = "Severity level must be less than or equal to 3")
  private int severityLevel;

  private String description;

  @NotNull(message = "Start date cannot be null")
  @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}",
          message = "Date must be in ISO format (yyyy-MM-ddTHH:mm:ss)")
  private String startDate;
}