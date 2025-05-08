package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NewsArticleRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private NewsArticleRepository repository;


  @Test
  void testGetAllNewsArticles_returnsAllSortedByPublishedAtDesc() {
    List<NewsArticle> articles = repository.getAllNewsArticles();

    assertThat(articles).hasSize(5);
    assertThat(articles.get(0).getTitle()).isEqualTo("Earthquake Strikes Eastern Turkey");
    assertThat(articles.get(1).getTitle()).isEqualTo("Severe Flooding in Southern Brazil");
    assertThat(articles.get(2).getTitle()).isEqualTo("Wildfires Rage in California");
  }

  @Test
  void testGetNewsArticleById_returnsCorrectArticle() {
    NewsArticle article = repository.getNewsArticleById(2L);

    assertThat(article).isNotNull();
    assertThat(article.getTitle()).isEqualTo("Severe Flooding in Southern Brazil");
    assertThat(article.getContent()).isEqualTo("Heavy rains have led to severe flooding in southern Brazil, displacing thousands and causing significant property damage. Emergency services are on high alert.");
    assertThat(article.getPublishedAt()).isEqualTo(LocalDateTime.of(2025, 5, 6, 10, 30));
  }
}