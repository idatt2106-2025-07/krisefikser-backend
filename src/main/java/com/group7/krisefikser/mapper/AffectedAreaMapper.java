package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.AffectedAreaRequest;
import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.model.AffectedArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between AffectedArea entity and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface AffectedAreaMapper {
  AffectedAreaMapper INSTANCE = Mappers.getMapper(AffectedAreaMapper.class);

  /**
   * Custom mapper method to convert String to LocalDateTime.
   *
   * @param date the date string to convert
   * @return the parsed LocalDateTime
   */
  @Named("stringToDateTime")
  default LocalDateTime stringToDateTime(String date) {
    if (date == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    } catch (Exception e) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return LocalDateTime.parse(date, formatter);
    }
  }

  /**
   * Maps the AffectedArea entity to a DTO.
   *
   * @param affectedArea the AffectedArea entity to map
   * @return the mapped affected area DTO
   */
  @Mapping(target = "id", source = "id")
  @Mapping(target = "longitude", source = "longitude")
  @Mapping(target = "latitude", source = "latitude")
  @Mapping(target = "highDangerRadiusKm", source = "highDangerRadiusKm")
  @Mapping(target = "mediumDangerRadiusKm", source = "mediumDangerRadiusKm")
  @Mapping(target = "lowDangerRadiusKm", source = "lowDangerRadiusKm")
  @Mapping(target = "severityLevel", source = "severityLevel")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "startDate", source = "startDate")
  AffectedAreaResponse affectedAreaToResponse(AffectedArea affectedArea);

  /**
   * Maps the AffectedArea DTO to an entity.
   *
   * @param affectedAreaRequest the AffectedArea DTO to map
   * @return the mapped affected area entity
   */
  @Mapping(target = "longitude", source = "longitude")
  @Mapping(target = "latitude", source = "latitude")
  @Mapping(target = "highDangerRadiusKm", source = "highDangerRadiusKm")
  @Mapping(target = "mediumDangerRadiusKm", source = "mediumDangerRadiusKm")
  @Mapping(target = "lowDangerRadiusKm", source = "lowDangerRadiusKm")
  @Mapping(target = "severityLevel", source = "severityLevel")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToDateTime")
  AffectedArea requestToAffectedArea(AffectedAreaRequest affectedAreaRequest);
}
