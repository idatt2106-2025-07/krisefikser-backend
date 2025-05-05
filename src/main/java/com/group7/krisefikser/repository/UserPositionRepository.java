package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.UserPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPositionRepository {

  private final JdbcTemplate jdbcTemplate;

  public void updateUserPosition(UserPosition userPosition) {
    String sql = "UPDATE user_position SET latitude = ?, longitude = ? WHERE user_id = ?";
    jdbcTemplate.update(
        sql, userPosition.getLatitude(), userPosition.getLongitude(), userPosition.getUserId());
  }

  public void addUserPosition(UserPosition userPosition) {
    String sql = "INSERT INTO user_position (latitude, longitude, user_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(
        sql, userPosition.getLatitude(), userPosition.getLongitude(), userPosition.getUserId());
  }

  public void deleteUserPosition(Long userId) {
    String sql = "DELETE FROM user_position WHERE user_id = ?";
    jdbcTemplate.update(sql, userId);
  }

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
}
