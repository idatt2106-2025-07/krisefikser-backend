package com.group7.krisefikser.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PrivacyPolicyRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private PrivacyPolicyRepository repository;

  @BeforeEach
  void setUp() {
    // Reset database to initial state before each test
    jdbcTemplate.update("UPDATE privacy_policy SET registered = ?, unregistered = ?",
        "placeholder_registered", "placeholder_unregistered");
  }

  @Test
  void testGetRegisteredPrivacyPolicy() {
    String registered = repository.getRegisteredPrivacyPolicy();
    assertEquals("placeholder_registered", registered);
  }

  @Test
  void testGetUnregisteredPrivacyPolicy() {
    String unregistered = repository.getUnregisteredPrivacyPolicy();
    assertEquals("placeholder_unregistered", unregistered);
  }

  @Test
  void testUpdateRegisteredPrivacyPolicy() {
    repository.updateRegisteredPrivacyPolicy("Updated Registered");
    String updated = repository.getRegisteredPrivacyPolicy();
    assertEquals("Updated Registered", updated);
  }

  @Test
  void testUpdateUnregisteredPrivacyPolicy() {
    repository.updateUnregisteredPrivacyPolicy("Updated Unregistered");
    String updated = repository.getUnregisteredPrivacyPolicy();
    assertEquals("Updated Unregistered", updated);
  }
}
