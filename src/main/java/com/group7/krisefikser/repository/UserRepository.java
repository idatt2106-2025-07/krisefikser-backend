package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class UserRepository {

  private final static Logger logger = Logger.getLogger(UserRepository.class.getName());

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    try {
      return jdbcTemplate.query(sql, (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setHouseholdId(rs.getLong("household_id"));
        user.setPassword(rs.getString("password"));
        String roleString = rs.getString("role").toUpperCase();
        Role role = Role.valueOf(roleString);
        user.setRole(role);
        return user;
      }, email).stream().findFirst();
    } catch (EmptyResultDataAccessException e) {
      logger.info("No user found with email: " + email);
      return Optional.empty();
    }
  }

  public Optional<User> save(User user) {
    if (user.getRole() == null) {
      user.setRole(Role.ROLE_NORMAL);
    }
    String query = "INSERT INTO users (email, name, household_id, password, role) VALUES (?, ?, ?, ?, ?)";
    try {
      int rowsAffected = jdbcTemplate.update(query, user.getEmail(), user.getName(), user.getHouseholdId(), user.getPassword(), user.getRole().toString());
      System.out.println("Rows affected by save: " + rowsAffected); // Add this debug line

      // Test if findByEmail works properly
      Optional<User> savedUser = findByEmail(user.getEmail());
      System.out.println("User found after save: " + (savedUser.isPresent() ? "Yes" : "No"));

      return savedUser;
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
