package com.group7.krisefikser.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NewsArticleResponse {
  private String title;
  private String content;
  private String publishedAt;
}
