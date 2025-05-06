package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.request.HcaptchaRequest;
import com.group7.krisefikser.dto.response.HcaptchaVerificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@RestController
@RequestMapping("/api/hcaptcha")
public class HcaptchaController {

    @Value("${hcaptcha.secret}")
    private String hcaptchaSecret;

   @PostMapping("/verify")
public ResponseEntity<?> verify(@RequestBody HcaptchaRequest request) {
    String token = request.getToken();
    String url = "https://hcaptcha.com/siteverify";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("secret", hcaptchaSecret);
    body.add("response", token);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

    ResponseEntity<HcaptchaVerificationResponse> response =
            restTemplate.exchange(url, HttpMethod.POST, entity, HcaptchaVerificationResponse.class);
    return ResponseEntity.ok(response.getBody());
}
}
