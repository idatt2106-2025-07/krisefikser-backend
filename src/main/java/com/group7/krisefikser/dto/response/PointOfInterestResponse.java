package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointOfInterestResponse {
  private Long id;
  private Double latitude;
  private Double longitude;
  private String type;
}
