package com.group7.krisefikser.dto.response;

import lombok.Data;

/**
 * This class represents the response sent to the client when a news article is requested.
 */
@Data
public class NewsArticleResponse {
  private String title;
  private String content;
  private String publishedAt;
}
