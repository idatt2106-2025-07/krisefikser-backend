package com.group7.krisefikser.repository.article;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing privacy policy data in the database.
 */
@Repository
@RequiredArgsConstructor
public class PrivacyPolicyRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * Retrieves the registered privacy policy from the database.
   *
   * @return The registered privacy policy as a String.
   */
  public String getRegisteredPrivacyPolicy() {
    String sql = "SELECT registered FROM privacy_policy";
    return jdbcTemplate.queryForObject(sql, String.class);
  }

  /**
   * Retrieves the unregistered privacy policy from the database.
   *
   * @return The unregistered privacy policy as a String.
   */
  public String getUnregisteredPrivacyPolicy() {
    String sql = "SELECT unregistered FROM privacy_policy";
    return jdbcTemplate.queryForObject(sql, String.class);
  }

  /**
   * Updates the registered privacy policy in the database.
   *
   * @param registered The new registered privacy policy as a String.
   */
  public void updateRegisteredPrivacyPolicy(String registered) {
    String sql = "UPDATE privacy_policy SET registered = ?";
    jdbcTemplate.update(sql, registered);
  }

  /**
   * Updates the unregistered privacy policy in the database.
   *
   * @param unregistered The new unregistered privacy policy as a String.
   */
  public void updateUnregisteredPrivacyPolicy(String unregistered) {
    String sql = "UPDATE privacy_policy SET unregistered = ?";
    jdbcTemplate.update(sql, unregistered);
  }
}
