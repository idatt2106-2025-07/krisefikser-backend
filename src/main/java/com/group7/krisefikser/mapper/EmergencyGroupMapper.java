package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.model.EmergencyGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Mapper interface for converting between EmergencyGroup and related DTO objects.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface EmergencyGroupMapper {
  EmergencyGroupMapper INSTANCE = Mappers.getMapper(EmergencyGroupMapper.class);

  /**
   * Maps the EmergencyGroup entity to a DTO.
   *
   * @param emergencyGroup the EmergencyGroup entity to map
   * @return the mapped EmergencyGroupResponse DTO
   */
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd")
  EmergencyGroupResponse emergencyGroupToResponse(EmergencyGroup emergencyGroup);

  /**
   * Maps the EmergencyGroupRequest DTO to an EmergencyGroup entity.
   *
   * @param emergencyGroupRequest the EmergencyGroupRequest DTO to map
   * @return the mapped EmergencyGroup entity
   */
  @Mapping(target = "name", source = "name")
  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "stringToSqlDate")
  EmergencyGroup emergencyGroupRequestToEntity(EmergencyGroupRequest emergencyGroupRequest);

  @Named("stringToSqlDate")
  default LocalDate stringToSqlDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }

    try {
      return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
