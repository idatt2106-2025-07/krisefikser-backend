package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.GeneralInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GeneralInfoRepository {
  private final JdbcTemplate jdbcTemplate;

  public GeneralInfoRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<GeneralInfo> getAllGeneralInfo() {
    String sql = "SELECT * FROM general_info";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      info.setTheme(Theme.valueOf(rs.getString("theme")));
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    });
  }

  public GeneralInfo getGeneralInfoById(Long id) {
    String sql = "SELECT * FROM general_info WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      info.setTheme(Theme.valueOf(rs.getString("theme")));
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    }, id);
  }

  public void addGeneralInfo(GeneralInfo info) {
    String sql = "INSERT INTO general_info (theme, title, content) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, info.getTheme().name(), info.getTitle(), info.getContent());
  }

  public void updateGeneralInfo(GeneralInfo info) {
    String sql = "UPDATE general_info SET theme = ?, title = ?, content = ? WHERE id = ?";
    jdbcTemplate.update(sql, info.getTheme().name(), info.getTitle(), info.getContent(), info.getId());
  }

  public void deleteGeneralInfo(Long id) {
    String sql = "DELETE FROM general_info WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  public List<GeneralInfo> getGeneralInfoByTheme(Theme theme) {
    String sql = "SELECT * FROM general_info WHERE theme = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      info.setTheme(Theme.valueOf(rs.getString("theme")));
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    }, theme.name());
  }

  public List<GeneralInfo> getGeneralInfoByTitle(String title) {
    String sql = "SELECT * FROM general_info WHERE title = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      info.setTheme(Theme.valueOf(rs.getString("theme")));
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    }, title);
  }


}
