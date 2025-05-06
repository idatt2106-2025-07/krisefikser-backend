// src/test/java/com/group7/krisefikser/config/TestRestTemplateConfig.java
package com.group7.krisefikser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestRestTemplateConfig {
    @Bean(name = "testRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
