package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.NewsArticleResponse;
import com.group7.krisefikser.dto.response.ShortenedNewsArticleResponse;
import com.group7.krisefikser.mapper.NewsArticleMapper;
import com.group7.krisefikser.model.NewsArticle;
import com.group7.krisefikser.repository.NewsArticleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to news articles.
 * This class provides methods to retrieve all news articles
 * and to get a specific article by its ID.
 */
@Service
@RequiredArgsConstructor
public class NewsArticleService {
  private final NewsArticleRepository newsArticleRepo;

  /**
   * Retrieves all news articles from the repository.
   * This method fetches the articles and returns them as a list.
   *
   * @return a list of NewsArticle objects containing details of all articles
   */
  public List<ShortenedNewsArticleResponse> getAllNewsArticles() {
    List<NewsArticle> articles = newsArticleRepo.getAllNewsArticles();
    return articles.stream()
        .map(NewsArticleMapper.INSTANCE::newsArticleToShortenedNewsArticleResponse)
        .toList();
  }

  /**
   * Retrieves a news article by its ID.
   * This method fetches the article from the repository
   * based on the provided ID.
   *
   * @param id the ID of the news article to be retrieved
   * @return the NewsArticle object containing details of the article
   */
  public NewsArticleResponse getNewsArticleById(long id) {
    NewsArticle article = newsArticleRepo.getNewsArticleById(id);
    if (article != null) {
      return NewsArticleMapper.INSTANCE.newsArticleToNewsArticleResponse(article);
    } else {
      throw new RuntimeException("News article not found with ID: " + id);
    }
  }
}
