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
}
