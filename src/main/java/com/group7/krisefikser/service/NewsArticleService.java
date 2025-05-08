package com.group7.krisefikser.service;

import com.group7.krisefikser.model.NewsArticle;
import com.group7.krisefikser.repository.GeneralInfoRepository;
import com.group7.krisefikser.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsArticleService {
  private final NewsArticleRepository newsArticleRepo;

  public List<NewsArticle> getAllNewsArticles() {
    return newsArticleRepo.getAllNewsArticles();
  }

  public NewsArticle getNewsArticleById(long id) {
    return newsArticleRepo.getNewsArticleById(id);
  }

}
