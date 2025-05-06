package com.group7.krisefikser.exception;

/**
 * Exception class for when an invitation is not found.
 * This exception is thrown when an attempt to retrieve an
 * invitation fails because the invitation does not exist.
 */
public class InvitationNotFoundException extends RuntimeException {
  /**
   * Constructs a new InvitationNotFoundException with the default message.
   */
  public InvitationNotFoundException(String message) {
    super(message);
  }
}
