package com.group7.krisefikser.enums;

public enum AuthResponseMessage {
  USER_NOT_FOUND("User not found"),
  INVALID_CREDENTIALS("Invalid credentials"),
  USER_ALREADY_EXISTS("User already exists"),
  USER_REGISTERED_SUCCESSFULLY("User registered successfully"),
  SAVING_USER_ERROR("Error saving user: "),
  USER_LOGGED_IN_SUCCESSFULLY("User logged in successfully"),
  USER_LOGIN_ERROR("Error logging in user: "),
  TOKEN_REFRESH_ERROR("Error refreshing token: "),
  INVALID_EMAIL_FORMAT("Invalid email format"),
  PASSWORD_TOO_WEAK("Password is too weak"),
  HOUSEHOLD_FAILURE("Household creation failed"),
  EMAIL_NOT_VERIFIED("Please verify your email before logging in");



  private final String message;

  AuthResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
