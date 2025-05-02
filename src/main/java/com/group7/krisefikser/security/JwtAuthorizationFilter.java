package com.group7.krisefikser.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.group7.krisefikser.exception.JwtMissingPropertyException;
import com.group7.krisefikser.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The JWT filter for authenticating a JWT authenticated request.
 */
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

  private final JwtUtils jwtUtils;


  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
  
    String path = request.getRequestURI();
    logger.info("JWTAuthorizationFilter called for URI: {}", path);
  
    // 🔐 Bypass token validation for public endpoints
    if (path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/register") ||
        path.startsWith("/api/auth/verify-email")) {
      logger.info("Skipping JWT validation for public path: {}", path);
      filterChain.doFilter(request, response);
      return;
    }
  
    String token = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("JWT".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }
  
    if (token == null) {
      logger.warn("No token found in request");
      filterChain.doFilter(request, response);
      return;
    }
  
    final String username;
    final String role;
    logger.info("Validating token");
    try {
      username = jwtUtils.validateTokenAndGetUserId(token);
      role = jwtUtils.validateTokenAndGetRole(token);
    } catch (JwtMissingPropertyException | JWTVerificationException e) {
      logger.warn("Invalid token: {}", e.getMessage());
      filterChain.doFilter(request, response);
      return;
    }
  
    if (username == null || role == null) {
      logger.warn("Token missing username or role");
      filterChain.doFilter(request, response);
      return;
    }
  
    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(role));
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        username,
        null,
        authorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
  
    logger.info("User: {}, role: {} has been authenticated", username, role);
    filterChain.doFilter(request, response);
  }
}