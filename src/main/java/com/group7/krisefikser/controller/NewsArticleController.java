package com.group7.krisefikser.controller;

import com.group7.krisefikser.model.NewsArticle;
import com.group7.krisefikser.service.NewsArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling operations related to news articles.
 * This class provides endpoints to retrieve all news articles
 * and to retrieve a specific news article by its ID.
 */
@RestController
@RequestMapping("/api/news")
@Tag(name = "News Articles", description = "Operations related to news articles")
public class NewsArticleController {

  private final NewsArticleService newsArticleService;
  private static final Logger logger = Logger.getLogger(NewsArticleController.class.getName());

  /**
   * Constructor for NewsArticleController.
   * Initializes the NewsArticleService instance.
   *
   * @param newsArticleService the NewsArticleService instance for handling news articles
   */
  @Autowired
  public NewsArticleController(NewsArticleService newsArticleService) {
    this.newsArticleService = newsArticleService;
  }

  /**
   * Retrieves all news articles.
   *
   * @return a ResponseEntity containing a list of NewsArticle objects
   */
  @Operation(
      summary = "Get all news articles",
      description = "Retrieves a list of all news articles sorted by publication date"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved all news articles",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = NewsArticle.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping
  public ResponseEntity<List<NewsArticle>> getAllNews() {
    logger.info("Fetching all news articles");
    try {
      List<NewsArticle> newsArticles = newsArticleService.getAllNewsArticles();
      return ResponseEntity.ok(newsArticles);
    } catch (Exception e) {
      logger.severe("Error fetching news articles: " + e.getMessage());
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Retrieves a specific news article by its ID.
   *
   * @param id the ID of the news article to be retrieved
   * @return a ResponseEntity containing the NewsArticle object
   */
  @Operation(
      summary = "Get news article by ID",
      description = "Retrieves a specific news article based on its ID"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved news article",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = NewsArticle.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "News article not found",
          content = @Content(mediaType = "application/json")
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("/{id}")
  public ResponseEntity<NewsArticle> getNewsById(@PathVariable Long id) {
    logger.info("Fetching news article with ID: " + id);
    try {
      NewsArticle newsArticle = newsArticleService.getNewsArticleById(id);
      if (newsArticle != null) {
        return ResponseEntity.ok(newsArticle);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      logger.severe("Error fetching news article with ID " + id + ": " + e.getMessage());
      return ResponseEntity.status(500).build();
    }
  }
}
