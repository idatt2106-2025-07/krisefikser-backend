package com.group7.krisefikser.service;

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
  public List<NewsArticle> getAllNewsArticles() {
    return newsArticleRepo.getAllNewsArticles();
  }

  /**
   * Retrieves a news article by its ID.
   * This method fetches the article from the repository
   * based on the provided ID.
   *
   * @param id the ID of the news article to be retrieved
   * @return the NewsArticle object containing details of the article
   */
  public NewsArticle getNewsArticleById(long id) {
    return newsArticleRepo.getNewsArticleById(id);
  }
}
