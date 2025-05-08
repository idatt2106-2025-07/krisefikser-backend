package com.group7.krisefikser.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for the hCaptcha request.
 * It contains a single field for the token received from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HcaptchaRequest {
  private String token;
}
