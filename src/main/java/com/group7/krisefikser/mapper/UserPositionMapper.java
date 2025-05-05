package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.model.UserPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPositionMapper {
  UserPositionMapper INSTANCE = Mappers.getMapper(UserPositionMapper.class);

  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  UserPosition sharePositionRequestToUserPosition(SharePositionRequest request);

  @Mapping(source = "latitude", target = "latitude")
  @Mapping(source = "longitude", target = "longitude")
  @Mapping(source = "name", target = "name")
  HouseholdMemberPositionResponse userPositionToHouseholdMemberPositionResponse(
      UserPosition userPosition);

  HouseholdMemberPositionResponse[] userPositionArrayToHouseholdMemberPositionResponseArray(
      UserPosition[] userPositions);
}
