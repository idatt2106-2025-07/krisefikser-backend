package com.group7.krisefikser.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a news article in the system.
 * It contains the article's information such as title, content, and published date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsArticle {
  Long id;
  String title;
  String content;
  LocalDateTime publishedAt;
}
