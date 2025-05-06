package com.group7.krisefikser.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for password hashing and verification.
 * This class provides methods to hash a password using BCrypt and to verify a password
 * against a hashed password.
 * It uses the BCryptPasswordEncoder from Spring Security for hashing and verifying passwords.
 */
public class PasswordUtil {

  private PasswordUtil() {}

  /**
   * Hashes a password using BCrypt.
   * This method takes a plain text password and returns the hashed version of it.
   *
   * @param password the plain text password to be hashed
   * @return the hashed password
   */
  public static String hashPassword(String password) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(password);
  }

  /**
   * Verifies a password against a hashed password.
   * This method takes a plain text password and a hashed password, and checks if they match.
   *
   * @param password the plain text password to be verified
   * @param hashedPassword the hashed password to verify against
   * @return true if the password matches the hashed password, false otherwise
   */
  public static boolean verifyPassword(String password, String hashedPassword) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.matches(password, hashedPassword);
  }

  /**
   * Checks if a password is strong.
   * A strong password is defined as one that contains at least one lowercase letter,
   * one uppercase letter, one digit, and is at least 8 characters long.
   *
   * @param password the password to check
   * @return true if the password is strong, false otherwise
   */
  public static boolean isStrongPassword(String password) {
    return password != null && password.matches(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

  }
}
