package com.group7.krisefikser.dto.response.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for the hCaptcha verification response.
 * It contains fields for the success status, challenge timestamp,
 * hostname, and any error codes returned by the hCaptcha API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HcaptchaVerificationResponse {
  private boolean success;

  @JsonProperty("challenge_ts")
  private String challengeTs;

  private String hostname;

  @JsonProperty("error-codes")
  private List<String> errorCodes;
}