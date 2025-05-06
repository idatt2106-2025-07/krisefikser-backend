package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.HcaptchaRequest;
import com.group7.krisefikser.dto.response.HcaptchaVerificationResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller for handling hCaptcha verification requests.
 * It verifies the hCaptcha token sent from the client by making a request to the hCaptcha API.
 * The controller is mapped to the "/api/hcaptcha" endpoint.
 * It contains a single POST endpoint "/verify" that accepts a JSON request body
 * with the hCaptcha token and returns the verification response.
 * The controller uses Spring's
 * RestTemplate to make the API call to hCaptcha's verification endpoint.
 * The hCaptcha secret key is injected from the application properties.
 */
@RestController
@RequestMapping("/api/hcaptcha")
public class HcaptchaController {


  private final RestTemplate restTemplate;

  /**
   * Constructor for HcaptchaController.
   * It initializes the RestTemplate used for making HTTP requests.
   *
   * @param restTemplate the RestTemplate to be used
   */
  public HcaptchaController(RestTemplate restTemplate) {

    this.restTemplate = restTemplate;

  }

  @Value("${hcaptcha.secret}")
  private String hcaptchaSecret;

  /**
   * Endpoint to verify the hCaptcha token.
   * It accepts a POST request with the hCaptcha token in the request body.
   * The token is then sent to the hCaptcha API for verification.
   *
   * @param request the request containing the hCaptcha token
   * @return the response from the hCaptcha API
   */
  @PostMapping("/verify")
  public ResponseEntity<?> verify(@RequestBody HcaptchaRequest request) {
    String token = request.getToken();
    final String url = "https://hcaptcha.com/siteverify";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("secret", hcaptchaSecret);
    body.add("response", token);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

    ResponseEntity<HcaptchaVerificationResponse> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, HcaptchaVerificationResponse.class);

    HcaptchaVerificationResponse result = response.getBody();

    HcaptchaVerificationResponse failure = HcaptchaVerificationResponse.builder()
        .success(false)
        .errorCodes(List.of("verification-failed"))
        .build();

    if (result != null && result.isSuccess()) {
      return ResponseEntity.ok(result);
    } else {
      return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(result != null ? result : failure);
    }
  }
}
