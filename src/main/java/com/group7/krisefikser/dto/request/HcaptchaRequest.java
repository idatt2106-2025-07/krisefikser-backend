package com.group7.krisefikser.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
