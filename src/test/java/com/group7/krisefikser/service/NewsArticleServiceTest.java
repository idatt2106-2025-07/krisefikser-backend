package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.NewsArticleResponse;
import com.group7.krisefikser.dto.response.ShortenedNewsArticleResponse;
import com.group7.krisefikser.model.NewsArticle;
import com.group7.krisefikser.repository.NewsArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NewsArticleServiceTest {

  private NewsArticleRepository newsArticleRepository;
  private NewsArticleService newsArticleService;

  @BeforeEach
  void setUp() {
    newsArticleRepository = Mockito.mock(NewsArticleRepository.class);
    newsArticleService = new NewsArticleService(newsArticleRepository);
  }

  @Test
  void testGetAllNewsArticles_returnsListFromRepository() {
    List<NewsArticle> mockArticles = List.of(
        new NewsArticle(1L, "Title 1", "Content 1", LocalDateTime.now()),
        new NewsArticle(2L, "Title 2", "Content 2", LocalDateTime.now().minusDays(1))
    );

    when(newsArticleRepository.getAllNewsArticles()).thenReturn(mockArticles);

    List<ShortenedNewsArticleResponse> result = newsArticleService.getAllNewsArticles();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getTitle()).isEqualTo("Title 1");
    verify(newsArticleRepository, times(1)).getAllNewsArticles();
  }

  @Test
  void testGetNewsArticleById_returnsCorrectArticle() {
    NewsArticle mockArticle = new NewsArticle(1L, "Mock Title", "Mock Content", LocalDateTime.now());

    when(newsArticleRepository.getNewsArticleById(1L)).thenReturn(mockArticle);

    NewsArticleResponse result = newsArticleService.getNewsArticleById(1L);

    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Mock Title");
    verify(newsArticleRepository, times(1)).getNewsArticleById(1L);
  }
}