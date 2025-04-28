package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Role;
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
    String sql = "SELECT * FROM users WHERE email = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setEmail(rs.getString("email"));
      user.setName(rs.getString("name"));
      user.setHouseholdId(rs.getLong("household_id"));
      user.setPassword(rs.getString("password"));
      String roleStr = rs.getString("role").toUpperCase();
      user.setRole(Role.valueOf(roleStr));
      return user;
    }, email).stream().findFirst();
  }

  public Optional<User> save(User user) {
    if (user.getRole() == null) {
      user.setRole(Role.NORMAL);
    }
    String query = "INSERT INTO users (email, name, household_id, password, role) VALUES (?, ?, ?, ?, ?)";
    try {
      jdbcTemplate.update(query, user.getEmail(), user.getName(), user.getHouseholdId(), user.getPassword(), user.getRole().toString());
      return findByEmail(user.getEmail());
    } catch (Exception e) {
      System.err.println("Failed to save user: " + e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Optional<Object> findById(Long id) {
    // Implementation to find user by id
    return null;
  }
}
