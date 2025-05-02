package com.group7.krisefikser.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericMessageResponse {

    @Schema(description = "Response message", example = "Password updated successfully")
    private String message;
}
