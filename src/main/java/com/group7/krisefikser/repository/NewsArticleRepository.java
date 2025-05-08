package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.NewsArticle;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for handling database operations related to news articles.
 * This class uses JdbcTemplate to interact with the database.
 */
@Repository
public class NewsArticleRepository {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for NewsArticleRepository.
   * Initializes the JdbcTemplate instance.
   *
   * @param jdbcTemplate the JdbcTemplate instance for database operations
   */
  public NewsArticleRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieves all news articles from the database.
   * This method fetches all news articles
   * and orders them by the published date in descending order.
   *
   * @return a list of NewsArticle objects containing details of the news articles
   */
  public List<NewsArticle> getAllNewsArticles() {
    String sql = "SELECT * FROM news_articles ORDER BY published_at DESC";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      NewsArticle article = new NewsArticle();
      article.setId(rs.getLong("id"));
      article.setTitle(rs.getString("title"));
      article.setContent(rs.getString("content"));
      article.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
      return article;
    });
  }

  /**
   * Retrieves a specific news article by its ID.
   * This method fetches the news article
   * with the specified ID from the database.
   *
   * @param id the ID of the news article to be retrieved
   * @return the NewsArticle object containing details of the news article
   */
  public NewsArticle getNewsArticleById(Long id) {
    String sql = "SELECT * FROM news_articles WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
      NewsArticle article = new NewsArticle();
      article.setId(rs.getLong("id"));
      article.setTitle(rs.getString("title"));
      article.setContent(rs.getString("content"));
      article.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
      return article;
    }, id);
  }
}
