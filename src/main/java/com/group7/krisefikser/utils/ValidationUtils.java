package com.group7.krisefikser.utils;

import com.group7.krisefikser.dto.response.ErrorResponse;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Utility class for handling validation errors.
 * This class provides methods to handle validation errors and return appropriate responses.
 */
public class ValidationUtils {

  /**
   * Private constructor to prevent instantiation of the utility class.
   */
  private ValidationUtils() {
  }

  private static final Logger logger = Logger.getLogger(ValidationUtils.class.getName());

  /**
   * Handles validation errors and returns a response entity with the error messages.
   *
   * @param bindingResult the BindingResult containing validation errors
   * @return a ResponseEntity containing the error messages
   */
  public static ResponseEntity<Object> handleValidationErrors(BindingResult bindingResult) {
    List<FieldError> errors = bindingResult.getFieldErrors();
    List<String> errorMessages = errors.stream()
            .map(FieldError::getDefaultMessage)
            .toList();
    logger.severe("Validation failed: " + String
            .join(", ", errorMessages));
    return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed: "
            + errorMessages));
  }
}