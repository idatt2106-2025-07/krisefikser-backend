package com.group7.krisefikser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the Krisefikser application.
 * This class serves as the entry point for the Spring Boot application.
 */
@SpringBootApplication
public class KrisefikserApplication {

  /**
   * Bean for RestTemplate, used for making HTTP requests.
   *
   * @return a new instance of RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * Main method to run the Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(KrisefikserApplication.class, args);
  }
}
