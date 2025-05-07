package com.group7.krisefikser.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrivacyPolicyRepository {

  private final JdbcTemplate jdbcTemplate;

  public String getRegisteredPrivacyPolicy() {
    String sql = "SELECT registered FROM privacy_policy";
    return jdbcTemplate.queryForObject(sql, String.class);
  }

  public String getUnregisteredPrivacyPolicy() {
    String sql = "SELECT unregistered FROM privacy_policy";
    return jdbcTemplate.queryForObject(sql, String.class);
  }

  public void updateRegisteredPrivacyPolicy(String registered) {
    String sql = "UPDATE privacy_policy SET registered = ?";
    jdbcTemplate.update(sql, registered);
  }

  public void updateUnregisteredPrivacyPolicy(String unregistered) {
      String sql = "UPDATE privacy_policy SET unregistered = ?";
      jdbcTemplate.update(sql, unregistered);
  }
}
