package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SharePositionRequest {
  @NotNull(message = "Latitude is required")
  @Min(value = -90, message = "Latitude must be greater than or equal to -90")
  @Max(value = 90, message = "Latitude must be less than or equal to 90")
  private double latitude;

  @NotNull(message = "Longitude is required")
  @Min(value = -180, message = "Longitude must be greater than or equal to -180")
  @Max(value = 180, message = "Longitude must be less than or equal to 180")
  private double longitude;
}
