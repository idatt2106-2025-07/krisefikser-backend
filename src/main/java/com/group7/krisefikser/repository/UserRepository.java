package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }



  public Optional<User> findByEmail(String email) {
    // Implementation to find user by email
    return null;
  }

  public Optional<User> save(User user) {
    // Implementation to save user
    return null;
  }
}
