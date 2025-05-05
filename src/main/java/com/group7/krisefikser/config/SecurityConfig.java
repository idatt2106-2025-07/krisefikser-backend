package com.group7.krisefikser.config;

import com.group7.krisefikser.security.JwtAuthorizationFilter;
import com.group7.krisefikser.utils.JwtUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for setting up authentication and authorization configurations.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtils jwtUtils;

  /**
   * Bean for the JWT authorization filter.
   *
   * @return the JwtAuthorizationFilter
   */
  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtUtils);
  }

  /**
   * Configuration for the filter chain, allowing for configuring endpoint authentication
   * and authorization requirements.
   *
   * @param http the HttpSecurity to modify
   * @return the configured SecurityChainFilter
   * @throws Exception if any error occurs while configuring the filter chain
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    http.cors(cors -> cors.configurationSource(source))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
                               
            .requestMatchers(HttpMethod.GET,
                "/api/affected-area",
                "/api/point-of-interest",
                "/h2-console/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/general-info/**", 
                "/api/auth/**")
            .permitAll()

            .requestMatchers(HttpMethod.POST,
                "/api/auth/**",
                "/api/admin/register",
                "/api/admin/2fa",
                "/h2-console/**")
            .permitAll()

            .requestMatchers(HttpMethod.DELETE,
                "/api/items/**")
            .hasAnyRole("ADMIN", "SUPER_ADMIN")

            .requestMatchers(
                "/api/point-of-interest/**",
                "/api/affected-area/**",
                "/api/general-info/admin/**")
            .hasAnyRole("SUPER_ADMIN", "ADMIN")

            .requestMatchers(
                "/api/super-admin/**"
            ).hasRole("SUPER_ADMIN")

            .anyRequest().authenticated())
        .headers(
            headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.addFilterBefore(
        jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
