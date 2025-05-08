package com.group7.krisefikser.controller;

import com.group7.krisefikser.dto.response.NewsArticleResponse;
import com.group7.krisefikser.dto.response.ShortenedNewsArticleResponse;
import com.group7.krisefikser.service.NewsArticleService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NewsArticleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @org.springframework.boot.test.mock.mockito.MockBean
  private NewsArticleService newsArticleService;

  @Test
  void testGetAllNews_ReturnsShortenedList() throws Exception {
    // Create objects using setters instead of constructors
    ShortenedNewsArticleResponse article1 = new ShortenedNewsArticleResponse();
    article1.setTitle("Title One");
    article1.setPublishedAt("01 May 2024, 10:00");

    ShortenedNewsArticleResponse article2 = new ShortenedNewsArticleResponse();
    article2.setTitle("Title Two");
    article2.setPublishedAt("02 May 2024, 14:00");

    List<ShortenedNewsArticleResponse> articles = Arrays.asList(article1, article2);

    when(newsArticleService.getAllNewsArticles()).thenReturn(articles);

    mockMvc.perform(get("/api/news").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].title").value("Title One"))
        .andExpect(jsonPath("$[0].publishedAt").value("01 May 2024, 10:00"));
  }

  @Test
  void testGetNewsById_ReturnsArticle() throws Exception {
    // Create object using setters instead of constructor
    NewsArticleResponse article = new NewsArticleResponse();
    article.setTitle("Full Title");
    article.setContent("Full Content");
    article.setPublishedAt("01 May 2024, 10:00");

    when(newsArticleService.getNewsArticleById(1L)).thenReturn(article);

    mockMvc.perform(get("/api/news/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Full Title"))
        .andExpect(jsonPath("$.content").value("Full Content"))
        .andExpect(jsonPath("$.publishedAt").value("01 May 2024, 10:00"));
  }

  @Test
  void testGetNewsById_NotFound() throws Exception {
    when(newsArticleService.getNewsArticleById(anyLong())).thenReturn(null);

    mockMvc.perform(get("/api/news/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAllNews_InternalServerError() throws Exception {
    when(newsArticleService.getAllNewsArticles()).thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get("/api/news").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void testGetNewsById_InternalServerError() throws Exception {
    when(newsArticleService.getNewsArticleById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(get("/api/news/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}