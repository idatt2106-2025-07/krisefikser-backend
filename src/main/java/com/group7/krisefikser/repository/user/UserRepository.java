package com.group7.krisefikser.repository.user;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.user.User;
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
      return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
              mapRowToUser(rs), email));
    } catch (EmptyResultDataAccessException e) {
      logger.info("No user found with email: " + email);
      return Optional.empty();
    }
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
   *      or an empty Optional if not
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
   * @param userId      the ID of the user whose household is being updated
   * @param householdId the ID of the household to associate with the user
   */
  public void updateUserHousehold(Long userId, Long householdId) {
    jdbcTemplate.update("DELETE FROM join_household_requests WHERE user_id = ?", userId);
    jdbcTemplate.update("UPDATE users SET household_id = ? WHERE id = ?", householdId, userId);
  }

  /**
   * Finds the household ID associated with a user.
   *
   * @param userId the ID of the user
   * @return the ID of the user's household, or null if not found
   */
  public Long findHouseholdIdByUserId(Long userId) {
    String sql = "SELECT household_id FROM users WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, Long.class, userId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  /**
   * Finds the name associated with a user ID.
   *
   * @param userId the ID of the user
   * @return the name of the user, or null if not found
   */
  public String findNameById(Long userId) {
    String sql = "SELECT name FROM users WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, String.class, userId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }
  
  /**
   * Deletes a user from the database by their ID.
   * This method removes the user from the users table
   * and also deletes any associated records
   * from the join_household_requests table.
   *
   * @param id the ID of the user to be deleted
   */
  public void deleteById(Long id) {
    jdbcTemplate.update("DELETE FROM join_household_requests WHERE user_id = ?", id);
    jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
  }

  /**
   * Finds users by their role.
   * This method queries the database for users with the specified role.
   * If users are found, it returns a list of users.
   * If no users are found, it returns an empty list.
   *
   * @param role the role of the users to be found
   * @return a list of users with the specified role, or an empty list if none are found
   */
  public List<User> findByRole(Role role) {
    String sql = "SELECT * FROM users WHERE role = ?";
    try {
      return jdbcTemplate.query(sql, (rs, rowNum) ->
          mapRowToUser(rs), role.toString());
    } catch (EmptyResultDataAccessException e) {
      logger.info("No users found with role: " + role);
      return List.of();
    }
  }

  /**
   * Updates a user's password in the database.
   * This method sets the password for a user with the specified email.
   * It is used for updating the password
   * when a user requests a password reset.
   *
   * @param email the email address of the user whose password is being updated
   * @param hashedPassword the new hashed password to be set
   */
  public void updatePasswordByEmail(String email, String hashedPassword) {
    String sql = "UPDATE users SET password = ? WHERE email = ?";
    jdbcTemplate.update(sql, hashedPassword, email);
  }

  /**
   * Retrieves a list of users associated with a specific household ID.
   * This method queries the database for users with the specified household ID.
   * If users are found, it returns a list of users.
   * If no users are found, it returns an empty list.
   *
   * @param householdId the ID of the household whose users are to be retrieved
   * @return a list of users associated with the specified household ID,
   *         or an empty list if none are found
   */
  public List<User> getUsersByHouseholdId(Long householdId) {
    String sql = "SELECT * FROM users WHERE household_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) ->
            mapRowToUser(rs), householdId);
  }
}
