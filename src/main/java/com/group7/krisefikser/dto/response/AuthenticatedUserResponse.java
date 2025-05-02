package com.group7.krisefikser.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUserResponse {

    @Schema(description = "The user's email address", example = "user@example.com")
    private String email;

    @Schema(description = "The user's role", example = "ROLE_USER")
    private String role;
}
