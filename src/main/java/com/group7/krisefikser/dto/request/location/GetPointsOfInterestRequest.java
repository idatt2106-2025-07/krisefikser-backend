package com.group7.krisefikser.dto.request.location;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Request class for getting points of interest.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
public class GetPointsOfInterestRequest {
  private List<String> types;
}
