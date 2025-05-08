package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to the client after
 * checking the readiness of a household for an emergency.
 * It contains the number of days and hours the household
 * can sustain itself based on the available resources.
 */
@Data
@AllArgsConstructor
public class ReadinessResponse {
  private int days;
  private int hours;
}
