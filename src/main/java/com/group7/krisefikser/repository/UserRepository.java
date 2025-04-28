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
    String sql = "SELECT * FROM user WHERE email = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setEmail(rs.getString("email"));
      user.setName(rs.getString("name"));
      user.setHouseholdId(rs.getLong("household_id"));
      user.setPassword(rs.getString("password"));
      user.setRole(Role.valueOf(rs.getString("role")));
      return user;
    }, email).stream().findFirst();
  }

  public Optional<User> save(User user) {
    String query = "INSERT INTO users (email, name, household_id, password, role) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(query, user.getEmail(), user.getName(), user.getHouseholdId(), user.getPassword(), user.getRole());
    return findByEmail(user.getEmail());
  }

  public Optional<Object> findById(Long id) {
    // Implementation to find user by id
    return null;
  }
}
