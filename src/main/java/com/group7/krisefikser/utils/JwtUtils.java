package com.group7.krisefikser.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.model.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utils class for JWT based tasks.
 */
@Component
public class JwtUtils {
  private static final String KEY_SECRET = "e256b71bd0e14db99e73badaeae8385c";
  private static final Duration JWT_VALIDITY = Duration.ofMinutes(120);

  private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  /**
   * generates a json web token based on the userID and role parameters.
   *
   * @param userId the subject of the token
   * @param role the authority of the token
   * @return a jwt for the user
   * @throws JwtMissingPropertyException if parameters are invalid
   */
  public String generateToken(final int userId, final Role role)
      throws JwtMissingPropertyException {
    if (role == null || userId <= 0) {
      throw new JwtMissingPropertyException("Token generation call must include UserId and Role");
    }
    final Instant now = Instant.now();
    final Algorithm hmac512 = Algorithm.HMAC512(KEY_SECRET);
    return JWT.create()
        .withSubject(String.valueOf(userId))
        .withIssuer("krisefikser")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(JWT_VALIDITY.toMillis()))
        .withClaim("role", role.toString())
        .sign(hmac512);
  }

  /**
   * validates a given token.
   *
   * @param token the jwt to be validated
   * @return the decoded jwt
   * @throws JWTVerificationException if the verification failed
   */
  private DecodedJWT validateToken(final String token) throws JWTVerificationException {
    try {
      final Algorithm hmac512 = Algorithm.HMAC512(KEY_SECRET);
      final JWTVerifier verifier = JWT.require(hmac512).build();
      return verifier.verify(token);
    } catch (final JWTVerificationException e) {
      logger.warn("token is invalid {}", e.getMessage());
      throw e;
    }
  }

  /**
   * validates and retrieves the user id from the given token.
   *
   * @param token the jwt to get user id from
   * @return the user id
   * @throws JwtMissingPropertyException if token doesn't contain a subject
   */
  public String validateTokenAndGetUserId(final String token) throws JwtMissingPropertyException {
    String subject = validateToken(token).getSubject();
    if (subject == null) {
      logger.error("Token does not contain a subject");
      throw new JwtMissingPropertyException("Token does not contain a subject");
    }
    return subject;
  }

  /**
   * validates and retrieves the role from the given token.
   *
   * @param token the jwt to get role from
   * @return the role
   * @throws JwtMissingPropertyException if token doesn't contain a role
   */
  public String validateTokenAndGetRole(final String token) throws JwtMissingPropertyException {
    String role = validateToken(token).getClaim("role").asString();
    if (role == null) {
      logger.error("Token does not contain a role");
      throw new JwtMissingPropertyException("Token does not contain a role");
    }
    return role;
  }

  /**
   * Sets a JWT token as an HTTP-only, secure cookie in the response.
   *
   * @param jwtToken The JWT token to be set in the cookie.
   * @param response The HttpServletResponse object to which the cookie will be added.
   */
  public void setJWTCookie(String jwtToken, HttpServletResponse response) {
    Cookie jwtCookie = new Cookie("JWT", jwtToken);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge((int) JWT_VALIDITY.getSeconds());
    response.addCookie(jwtCookie);
  }


  /**
   * Sets a logout JWT cookie in the HTTP response to effectively log out the user.
   * The cookie is configured to expire immediately, ending the client's session.
   *
   * @param response The HttpServletResponse object to which the cookie will be added.
   */
  public void setLogOutJWTCookie(HttpServletResponse response) {
    Cookie jwtCookie = new Cookie("JWT", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0);
    response.addCookie(jwtCookie);
  }
}

