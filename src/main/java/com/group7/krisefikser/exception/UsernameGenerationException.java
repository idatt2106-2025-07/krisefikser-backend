package com.group7.krisefikser.exception;

/**
 * Exception class for when username generation faults.
 */
public class UsernameGenerationException extends Exception {

  /**
   * Constructs a new UsernameGenerationException with the default message.
   */
  public UsernameGenerationException() {
    super("Username generation attempt failed.");
  }

  /**
   * Constructs a new UsernameGenerationException with the specified message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public UsernameGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new UsernameGenerationException with the specified message.
   *
   * @param message the message
   */
  public UsernameGenerationException(String message) {
    super(message);
  }
}
