package com.group7.krisefikser.utils;

import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for generating UUIDs.
 * This class provides methods to generate unique identifiers for various purposes.
 */
public class UuidUtils {

  /**
   * Generates a shortened UUID for the admin username.
   *
   * @return A shortened UUID as a string.
   */
  public static String generateShortenedUuid() {
    UUID uuid = UUID.randomUUID();

    return Base64.getEncoder()
        .encodeToString(uuid.toString().getBytes())
        .replaceAll("[=+/]", "")
        .substring(0, 8);
  }
}
