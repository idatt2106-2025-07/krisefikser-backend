package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.response.NewsArticleResponse;
import com.group7.krisefikser.dto.response.ShortenedNewsArticleResponse;
import com.group7.krisefikser.model.NewsArticle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper
public interface NewsArticleMapper {
  NewsArticleMapper INSTANCE = Mappers.getMapper(NewsArticleMapper.class);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "publishedAt", target = "publishedAt",
      qualifiedByName = "localDateTimeToString")
  ShortenedNewsArticleResponse newsArticleToShortenedNewsArticleResponse(
      NewsArticle newsArticle);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "publishedAt", target = "publishedAt",
      qualifiedByName = "localDateTimeToString")
  NewsArticleResponse newsArticleToNewsArticleResponse(NewsArticle newsArticle);

  @Named("localDateTimeToString")
  default String localDateTimeToString(LocalDateTime localDateTime) {
    return localDateTime.toString();
  }
}
