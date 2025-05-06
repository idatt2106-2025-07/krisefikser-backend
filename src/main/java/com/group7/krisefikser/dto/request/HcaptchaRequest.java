package com.group7.krisefikser.dto.request;

/**
 * DTO class for the hCaptcha request.
 * It contains a single field for the token received from the client.
 * The token is used to verify the hCaptcha response with the hCaptcha API.
 */
public class HcaptchaRequest {
  private String token;

  // Getter and setter
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
