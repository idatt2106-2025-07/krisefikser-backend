package com.group7.krisefikser.mapper.article;

import com.group7.krisefikser.dto.response.article.NewsArticleResponse;
import com.group7.krisefikser.dto.response.article.ShortenedNewsArticleResponse;
import com.group7.krisefikser.model.article.NewsArticle;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between NewsArticle and its DTO representations.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface NewsArticleMapper {
  NewsArticleMapper INSTANCE = Mappers.getMapper(NewsArticleMapper.class);

  /**
   * Converts a NewsArticle object to a ShortenedNewsArticleResponse object.
   * This method maps the fields of NewsArticle to the
   * corresponding fields of ShortenedNewsArticleResponse.
   *
   * @param newsArticle the NewsArticle object to convert
   * @return the converted ShortenedNewsArticleResponse object
   */
  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "publishedAt", target = "publishedAt",
      qualifiedByName = "localDateTimeToString")
  ShortenedNewsArticleResponse newsArticleToShortenedNewsArticleResponse(
      NewsArticle newsArticle);

  /**
   * Converts a NewsArticle object to a NewsArticleResponse object.
   * This method maps the fields of NewsArticle to the corresponding fields of NewsArticleResponse.
   *
   * @param newsArticle the NewsArticle object to convert
   * @return the converted NewsArticleResponse object
   */
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "publishedAt", target = "publishedAt",
      qualifiedByName = "localDateTimeToString")
  NewsArticleResponse newsArticleToNewsArticleResponse(NewsArticle newsArticle);

  /**
   * Custom mapper method to convert LocalDateTime to String.
   * This method handles the conversion of a LocalDateTime
   * to its string representation.
   *
   * @param localDateTime the LocalDateTime object to convert
   * @return the string representation of the LocalDateTime
   */
  @Named("localDateTimeToString")
  default String localDateTimeToString(LocalDateTime localDateTime) {
    return localDateTime.toString();
  }
}
