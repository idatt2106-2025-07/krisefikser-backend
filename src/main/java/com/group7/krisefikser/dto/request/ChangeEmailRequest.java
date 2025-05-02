package com.group7.krisefikser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ChangeEmailRequest {
    @NotBlank(message = "Emaoil cannot be blank")
    private String newEmail;

    public String getNewEmail() {
        return newEmail;
    }
}
