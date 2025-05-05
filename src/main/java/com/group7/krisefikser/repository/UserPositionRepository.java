package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.UserPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing user positions in the database.
 * Provides methods to update, add, delete, and check user positions.
 */
@Repository
@RequiredArgsConstructor
public class UserPositionRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * Updates the user's position in the database.
   *
   * @param userPosition The UserPosition object containing the updated position details.
   */
  public void updateUserPosition(UserPosition userPosition) {
    String sql = "UPDATE user_position SET latitude = ?, longitude = ? WHERE user_id = ?";
    jdbcTemplate.update(
        sql, userPosition.getLatitude(), userPosition.getLongitude(), userPosition.getUserId());
  }

  /**
   * Adds a new user position to the database.
   *
   * @param userPosition The UserPosition object containing the position details.
   */
  public void addUserPosition(UserPosition userPosition) {
    String sql = "INSERT INTO user_position (latitude, longitude, user_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(
        sql, userPosition.getLatitude(), userPosition.getLongitude(), userPosition.getUserId());
  }

  /**
   * Deletes the user's position from the database.
   *
   * @param userId The ID of the user whose position is to be deleted.
   */
  public void deleteUserPosition(Long userId) {
    String sql = "DELETE FROM user_position WHERE user_id = ?";
    jdbcTemplate.update(sql, userId);
  }

  /**
   * Checks if the user is sharing their position.
   *
   * @param userId The ID of the user to check.
   * @return true if the user is sharing their position, false otherwise.
   */
  public boolean isSharingPosition(Long userId) {
    String sql =
        "SELECT CASE "
            + "WHEN EXISTS ( "
            + "SELECT 1 FROM user_position WHERE user_id = ? "
            + ") THEN 'true' "
            + "ELSE 'false' "
            + "END AS finnes";
    return jdbcTemplate.queryForObject(
        sql,
        new Object[] {userId},
        Boolean.class);
  }

  /**
   * Retrieves the positions of all household members except the user themselves.
   *
   * @param userId The ID of the user whose household positions are to be retrieved.
   * @return An array of UserPosition objects containing the positions of household members.
   */
  public UserPosition[] getHouseholdPositions(Long userId) {
    String sql =
        "SELECT *, users.name FROM user_position "
        + "JOIN users ON user_position.user_id = users.id "
        + "WHERE users.household_id = "
        + "(SELECT household_id FROM users WHERE id = ?) "
        + "AND users.id != ?";

    return jdbcTemplate.query(
        sql,
        new Object[] {userId, userId},
        new BeanPropertyRowMapper<>(UserPosition.class)).toArray(new UserPosition[0]);
  }
}
