package com.group7.krisefikser.dto.response.article;

import lombok.Data;

/**
 * This class represents the response sent to the client when a shortened news article is requested.
 */
@Data
public class ShortenedNewsArticleResponse {
  private Long id;
  private String title;
  private String publishedAt;
}
