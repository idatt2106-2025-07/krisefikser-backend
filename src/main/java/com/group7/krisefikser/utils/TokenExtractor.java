package com.group7.krisefikser.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for extracting tokens from HTTP requests.
 * This class provides methods to extract a token from the Authorization header
 * or directly from an HttpServletRequest object.
 */
public class TokenExtractor {

  /**
   * Private constructor to prevent instantiation.
   */
  private TokenExtractor() {
  }

  /**
   * Extracts the token from the Authorization header.
   *
   * @param authorizationHeader the Authorization header
   * @return the extracted token, or null if the header is not present or does not
   *         start with "Bearer "
   */
  public static String extractToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  /**
   * Extracts the token from the HttpServletRequest.
   *
   * @param request the HttpServletRequest
   * @return the extracted token, or null if the header is not present or does not start
   *         with "Bearer "
   */
  public static String extractToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    return extractToken(authorizationHeader);
  }
}
