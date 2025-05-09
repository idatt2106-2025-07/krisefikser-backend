package com.group7.krisefikser.mapper.household;

import com.group7.krisefikser.dto.request.household.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.household.EmergencyGroupResponse;
import com.group7.krisefikser.model.household.EmergencyGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

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
  EmergencyGroup emergencyGroupRequestToEntity(EmergencyGroupRequest emergencyGroupRequest);
}
