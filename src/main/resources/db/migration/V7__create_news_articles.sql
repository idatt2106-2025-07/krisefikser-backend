CREATE TABLE news_articles (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      title VARCHAR(255) NOT NULL,
      content TEXT NOT NULL,
      published_at DATETIME NOT NULL
);