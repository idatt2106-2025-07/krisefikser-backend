package com.group7.krisefikser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception class to handle resource not found scenarios.
 * This exception is thrown when a requested resource is not found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  /**
     * Constructor for ResourceNotFoundException.
     *
     * @param message the detail message explaining the reason for the exception
     */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}