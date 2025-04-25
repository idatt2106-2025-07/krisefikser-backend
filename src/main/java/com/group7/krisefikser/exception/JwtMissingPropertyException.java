package com.group7.krisefikser.exception;

/**
 * Exception class for when a JWT is missing a property.
 */
public class JwtMissingPropertyException extends Exception {

  /**
   * Constructs a new JwtMissingSubjectException with the default message.
   */
  public JwtMissingPropertyException() {
    super("JWT missing a property.");
  }

  /**
   * Constructs a new JwtMissingSubjectException with the specified message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public JwtMissingPropertyException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new JwtMissingSubjectException with the specified message.
   *
   * @param message the message
   */
  public JwtMissingPropertyException(String message) {
    super(message);
  }
}
