package com.group7.krisefikser.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utils class for JWT based tasks.
 */
@Component
public class JwtUtils {
  private final String secretKey;
  private final String inviteSecretKey;
  private static final Duration JWT_VALIDITY = Duration.ofMinutes(120);
  private static final Duration JWT_INVITE_VALIDITY = Duration.ofMinutes(60);


  private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  /**
   * Constructor for JwtUtils, generates two secret keys.
   *
   * @throws NoSuchAlgorithmException if the algorithm is not found
   */
  public JwtUtils() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

    SecretKey sk = keyGen.generateKey();
    secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());

    SecretKey isk = keyGen.generateKey();
    inviteSecretKey = Base64.getEncoder().encodeToString(isk.getEncoded());
  }

  /**
   * This method retrieves the secret key used to sign the JWT tokens.
   *
   * @return The secret key as an Algorithm object.
   */
  Algorithm getKey(String key) {
    byte[] keyBytes = Base64.getDecoder().decode(key);
    return Algorithm.HMAC512(keyBytes);
  }

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
    return JWT.create()
        .withSubject(String.valueOf(userId))
        .withIssuer("krisefikser")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(JWT_VALIDITY.toMillis()))
        .withClaim("role", role.toString())
        .sign(getKey(secretKey));
  }

  public String generateInviteToken(final String username)
      throws JwtMissingPropertyException {
    if (username == null || username.isEmpty()) {
      throw new JwtMissingPropertyException("Invite token generation call must include username");
    }
    final Instant now = Instant.now();
    return JWT.create()
        .withSubject(username)
        .withIssuer("krisefikser")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(JWT_INVITE_VALIDITY.toMillis()))
        .withClaim("role", "ROLE_INVITE")
        .sign(getKey(inviteSecretKey));
  }

  /**
   * validates a given token.
   *
   * @param token the jwt to be validated
   * @return the decoded jwt
   * @throws JWTVerificationException if the verification failed
   */
  private DecodedJWT validateToken(final String token, String key) throws JWTVerificationException {
    try {
      final JWTVerifier verifier = JWT.require(getKey(key)).build();
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
    String subject = validateToken(token, secretKey).getSubject();
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
    String role = validateToken(token, secretKey).getClaim("role").asString();
    if (role == null) {
      logger.error("Token does not contain a role");
      throw new JwtMissingPropertyException("Token does not contain a role");
    }
    return role;
  }

  public String validateInviteTokenAndGetUsername(final String token) throws JwtMissingPropertyException {
    String username = validateToken(token, inviteSecretKey).getSubject();
    if (username == null) {
      logger.error("Token does not contain a subject");
      throw new JwtMissingPropertyException("Token does not contain a subject");
    }
    return username;
  }

  /**
   * Sets a JWT token as an HTTP-only, secure cookie in the response.
   *
   * @param jwtToken The JWT token to be set in the cookie.
   * @param response The HttpServletResponse object to which the cookie will be added.
   */
  public void setJwtCookie(String jwtToken, HttpServletResponse response) {
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
  public void setLogOutJwtCookie(HttpServletResponse response) {
    Cookie jwtCookie = new Cookie("JWT", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0);
    response.addCookie(jwtCookie);
  }
}

