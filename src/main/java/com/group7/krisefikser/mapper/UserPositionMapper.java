package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.GroupMemberPositionResponse;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.model.UserPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between SharePositionRequest,
 * UserPosition, and HouseholdMemberPositionResponse.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper
public interface UserPositionMapper {
  UserPositionMapper INSTANCE = Mappers.getMapper(UserPositionMapper.class);

  /**
   * Converts a SharePositionRequest to a UserPosition.
   *
   * @param request the SharePositionRequest to convert
   * @return the converted UserPosition
   */
  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  UserPosition sharePositionRequestToUserPosition(SharePositionRequest request);

  /**
   * Converts a UserPosition to a HouseholdMemberPositionResponse.
   *
   * @param userPosition the UserPosition to convert
   * @return the converted HouseholdMemberPositionResponse
   */
  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  @Mapping(source = "name", target = "name")
  HouseholdMemberPositionResponse userPositionToHouseholdMemberPositionResponse(
      UserPosition userPosition);

  /**
   * Converts an array of UserPosition to an array of HouseholdMemberPositionResponse.
   *
   * @param userPositions the array of UserPosition to convert
   * @return the converted array of HouseholdMemberPositionResponse
   */
  HouseholdMemberPositionResponse[] userPositionArrayToHouseholdMemberPositionResponseArray(
      UserPosition[] userPositions);

  /**
   * Converts a UserPosition to a GroupMemberPositionResponse.
   *
   * @param userPosition the UserPosition to convert
   * @return the converted GroupMemberPositionResponse
   */
  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  @Mapping(source = "name", target = "name")
  GroupMemberPositionResponse userPositionToGroupMemberPositionResponse(
      UserPosition userPosition);

  /**
   * Converts an array of UserPosition to an array of GroupMemberPositionResponse.
   *
   * @param userPositions the array of UserPosition to convert
   * @return the converted array of GroupMemberPositionResponse
   */
  GroupMemberPositionResponse[] userPositionArrayToGroupMemberPositionResponseArray(
      UserPosition[] userPositions);
}
