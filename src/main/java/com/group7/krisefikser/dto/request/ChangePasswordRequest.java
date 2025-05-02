package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
}
