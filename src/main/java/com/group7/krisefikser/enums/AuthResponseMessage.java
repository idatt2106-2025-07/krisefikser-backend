package com.group7.krisefikser.enums;

/**
 * Enum representing various authentication response messages.
 * These messages are used to provide feedback to the user during the authentication process.
 */
public enum AuthResponseMessage {
  USER_NOT_FOUND("User not found"),
  INVALID_CREDENTIALS("Invalid credentials"),
  USER_ALREADY_EXISTS("User already exists"),
  USER_REGISTERED_SUCCESSFULLY("User registered successfully"),
  SAVING_USER_ERROR("Error saving user: "),
  USER_LOGGED_IN_SUCCESSFULLY("User logged in successfully"),
  USER_LOGIN_ERROR("Error logging in user: "),
  TOKEN_REFRESH_ERROR("Error refreshing token: "),
  TOKEN_REFRESH_SUCCESS("Token refreshed successfully"),
  INVALID_EMAIL_FORMAT("Invalid email format"),
  PASSWORD_TOO_WEAK("Password is too weak"),
  HOUSEHOLD_FAILURE("Household creation failed"),
  EMAIL_NOT_VERIFIED("Please verify your email before logging in"),
  TWO_FACTOR_SENT("Two-factor authentication code sent"),
  USER_NOT_VERIFIED("Please verify your email before logging in"),
  USER_VERIFIED_SUCCESSFULLY("User verified successfully"),
  INVALID_TOKEN("Invalid token"),
  EMAIL_VERIFICATION_ERROR("Error verifying email: "),
  USER_ACCOUNT_BLOCKED("User account is blocked");



  private final String message;

  AuthResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
