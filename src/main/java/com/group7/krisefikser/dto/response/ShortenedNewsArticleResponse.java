package com.group7.krisefikser.dto.response;

import lombok.Data;

/**
 * This class represents the response sent to the client when a shortened news article is requested.
 */
@Data
public class ShortenedNewsArticleResponse {
  private String title;
  private String publishedAt;
}
