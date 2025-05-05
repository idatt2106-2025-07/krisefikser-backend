package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing user data in the database.
 * This class provides methods to find users by email,
 * save new users, and find users by ID.
 */
@Repository
public class UserRepository {

  private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for UserRepository.
   * This constructor initializes the JdbcTemplate used for database operations.
   *
   * @param jdbcTemplate the JdbcTemplate to be used for database operations
   */
  @Autowired
  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Finds a user by their email address.
   * This method queries the database for a user with the specified email.
   * If a user is found, it returns an Optional containing the user.
   * If no user is found, it returns an empty Optional.
   *
   * @param email the email address of the user to be found
   * @return an Optional containing the user if found, or an empty Optional if not found
   */
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
        user.setVerified(rs.getBoolean("verified"));
        return user;
      }, email).stream().findFirst();
    } catch (EmptyResultDataAccessException e) {
      logger.info("No user found with email: " + email);
      return Optional.empty();
    }
  }

  /**
   * Saves a new user to the database.
   * This method inserts a new user into the users table.
   * If the user does not have a role, it sets the role to ROLE_NORMAL by default.
   * If the user is saved successfully, it returns an Optional containing the saved user.
   * If an error occurs during the save operation, it returns an empty Optional.
   *
   * @param user the user to be saved
   * @return an Optional containing the saved user if successful, or an empty Optional if not
   */
  public Optional<User> save(User user) {
    if (user.getRole() == null) {
      user.setRole(Role.ROLE_NORMAL);
    }
    String query = "INSERT INTO users "
        + "(email, name, household_id, password, role) "
        + "VALUES (?, ?, ?, ?, ?)";
    try {
      jdbcTemplate.update(query, user.getEmail(), user.getName(),
          user.getHouseholdId(), user.getPassword(), user.getRole().toString());
      return findByEmail(user.getEmail());
    } catch (Exception e) {
      logger.info("Failed to save user: " + e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Changes a user's verified status in the database.
   * This method updates the verified status of a user
   * based on their email address.
   * If the update is successful, it returns an Optional
   * containing the updated user.
   *
   * @param user the user whose verified status is to be changed
   * @return an Optional containing the updated user if successful,
   *         or an empty Optional if not
   */
  public Optional<User> setVerified(User user) {
    String query = "UPDATE users SET verified = ? WHERE email = ?";
    try {
      jdbcTemplate.update(query, user.getVerified(), user.getEmail());
      return findByEmail(user.getEmail());
    } catch (Exception e) {
      logger.info("Failed to update verified: " + e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
  }

  /**
   * Checks the existence of an admin by their username.
   * This method queries the database for an admin with the specified username.
   * If an admin is found, it returns a true.
   * If no admin is found, it returns false.
   *
   * @param username the username of the admin to be found
   * @return a bool if found
   */
  public boolean existAdminByUsername(String username) {
    String sql =
        "SELECT CASE "
        + "WHEN EXISTS ( "
        + "SELECT 1 FROM users WHERE users.name = ? AND users.role = 'ROLE_ADMIN' "
        + ") THEN 'true' "
        + "ELSE 'false' "
        + "END AS finnes";
    return jdbcTemplate.queryForObject(
        sql,
        new Object[]{username},
        Boolean.class);
  }

  /**
   * Updates a user's household association in the database.
   * This method sets the household_id for a user with the specified user ID.
   *
   * @param userId the ID of the user whose household is being updated
   * @param householdId the ID of the household to associate with the user
   */
  public void updateUserHousehold(Long userId, Long householdId) {
    jdbcTemplate.update(
        "UPDATE users SET household_id = ? WHERE id = ?",
        householdId, userId);
  }

  public void deleteById(Long id) {
    jdbcTemplate.update("DELETE FROM join_household_requests WHERE user_id = ?", id);
    jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
  }

  private User mapRowToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setEmail(rs.getString("email"));
    user.setName(rs.getString("name"));
    user.setHouseholdId(rs.getLong("household_id"));
    user.setPassword(rs.getString("password"));
    String roleString = rs.getString("role").toUpperCase();
    Role role = Role.valueOf(roleString);
    user.setRole(role);
    user.setVerified(rs.getBoolean("verified"));
    return user;
  }

  /**
   * Finds a user by their ID.
   * This method queries the database for a user with the specified ID.
   * If a user is found, it returns an Optional containing the user.
   * If no user is found, it returns an empty Optional.
   *
   * @param id the ID of the user to be found
   * @return an Optional containing the user if found, or an empty Optional if not found
   */
  public Optional<User> findById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    try {
      return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
          mapRowToUser(rs), id));
    } catch (EmptyResultDataAccessException e) {
      logger.info("No user found with ID: " + id);
      return Optional.empty();
    }
  }

  public List<User> findByRole(Role role) {
    String sql = "SELECT * FROM users WHERE role = ?";
    try {
      return Optional.of(jdbcTemplate.query(sql, (rs, rowNum) ->
          mapRowToUser(rs), role.toString())).orElse(List.of());
    } catch (EmptyResultDataAccessException e) {
      logger.info("No users found with role: " + role);
      return List.of();
    }
  }
}
