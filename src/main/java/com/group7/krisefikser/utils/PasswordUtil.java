package com.group7.krisefikser.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

  private PasswordUtil() {}

  public static String hashPassword(String password) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(password);
  }

  public static boolean verifyPassword(String password, String hashedPassword) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.matches(password, hashedPassword);
  }
}
