package com.group7.krisefikser.controller;

import com.group7.krisefikser.model.NewsArticle;
import com.group7.krisefikser.service.NewsArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NewsArticleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NewsArticleService newsArticleService;


  @Test
  void getAllNews_shouldReturnListOfNewsArticles() throws Exception {
    List<NewsArticle> articles = List.of(
        new NewsArticle(1L, "Title 1", "Content 1", LocalDateTime.now()),
        new NewsArticle(2L, "Title 2", "Content 2", LocalDateTime.now().minusHours(1))
    );

    when(newsArticleService.getAllNewsArticles()).thenReturn(articles);

    mockMvc.perform(get("/api/news")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].title").value("Title 1"));
  }

  @Test
  void getNewsById_existingId_shouldReturnNewsArticle() throws Exception {
    NewsArticle article = new NewsArticle(1L, "Title", "Content", LocalDateTime.now());

    when(newsArticleService.getNewsArticleById(1L)).thenReturn(article);

    mockMvc.perform(get("/api/news/1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Title"))
        .andExpect(jsonPath("$.content").value("Content"));
  }

  @Test
  void getNewsById_nonExistingId_shouldReturnNotFound() throws Exception {
    when(newsArticleService.getNewsArticleById(99L)).thenReturn(null);

    mockMvc.perform(get("/api/news/99")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllNews_whenServiceThrowsException_shouldReturnServerError() throws Exception {
    when(newsArticleService.getAllNewsArticles()).thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get("/api/news")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getNewsById_whenServiceThrowsException_shouldReturnServerError() throws Exception {
    when(newsArticleService.getNewsArticleById(1L)).thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get("/api/news/1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}