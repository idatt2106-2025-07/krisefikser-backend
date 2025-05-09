package com.group7.krisefikser.repository.article;

import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.article.GeneralInfo;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing general information in the database.
 * This class provides methods to perform CRUD operations
 * on the general_info table.
 * It uses JdbcTemplate for database interactions.
 */
@Repository
public class GeneralInfoRepository {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for GeneralInfoRepository.
   * Initializes the JdbcTemplate for database operations.
   *
   * @param jdbcTemplate the JdbcTemplate to be used for database operations
   */
  public GeneralInfoRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieves all general information from the database.
   *
   * @return a list of GeneralInfo objects containing details of all general information
   */
  public List<GeneralInfo> getAllGeneralInfo() {
    String sql = "SELECT * FROM general_info";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      String themeString = rs.getString("theme").toUpperCase();
      Theme theme = Theme.valueOf(themeString);
      info.setTheme(theme);
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    });
  }

  /**
   * Adds a new general information entry to the database.
   * This method inserts a new record into the general_info table.
   *
   * @param info the GeneralInfo object containing details of the general information to be added
   * @return the added GeneralInfo object with the generated ID
   */
  public GeneralInfo addGeneralInfo(GeneralInfo info) {
    String sql = "INSERT INTO general_info (theme, title, content) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, info.getTheme().name(),
        info.getTitle(), info.getContent());
    Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    info.setId(id);
    return info;
  }

  /**
   * Updates an existing general information entry in the database.
   * This method modifies an existing record in the general_info table.
   *
   * @param info the GeneralInfo object containing updated details of the general information
   *             to be updated
   * @param id   the ID of the general information entry to be updated
   * @return the updated GeneralInfo object
   */
  public GeneralInfo updateGeneralInfo(GeneralInfo info, Long id) {
    String sql = "UPDATE general_info SET theme = ?, title = ?, content = ? WHERE id = ?";
    jdbcTemplate.update(sql, info.getTheme().name(),
        info.getTitle(), info.getContent(), id);
    info.setId(id);
    return info;
  }

  /**
   * Deletes a general information entry from the database.
   * This method removes a record from the general_info table
   * based on the provided ID.
   *
   * @param id the ID of the general information entry to be deleted
   */
  public void deleteGeneralInfo(Long id) {
    String sql = "DELETE FROM general_info WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  /**
   * Retrieves general information entries from the database
   * based on the specified theme.
   * This method returns a list of GeneralInfo objects
   * that match the provided theme.
   *
   * @param themeSearched the theme to search for in the general information
   * @return a list of GeneralInfo objects containing details of the matching general information
   */
  public List<GeneralInfo> getGeneralInfoByTheme(Theme themeSearched) {
    String sql = "SELECT * FROM general_info WHERE theme = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      GeneralInfo info = new GeneralInfo();
      info.setId(rs.getLong("id"));
      String themeString = rs.getString("theme").toUpperCase();
      Theme theme = Theme.valueOf(themeString);
      info.setTheme(theme);
      info.setTitle(rs.getString("title"));
      info.setContent(rs.getString("content"));
      return info;
    }, themeSearched.name());
  }
}
