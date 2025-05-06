package com.group7.krisefikser.dto.request;

import lombok.Data;

/**
 * DTO class for the hCaptcha request.
 * It contains a single field for the token received from the client.
 */
@Data
public class HcaptchaRequest {
  private String token;
}
