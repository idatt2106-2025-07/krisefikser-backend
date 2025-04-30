package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents an error response that is sent back to the client
 * when an error occurs in the application.
 * It contains a message to provide more information about the error.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
  private String message;
}
