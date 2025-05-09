package com.group7.krisefikser.mapper.household;

import com.group7.krisefikser.dto.request.household.JoinHouseholdRequest;
import com.group7.krisefikser.dto.response.household.JoinHouseholdRequestResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between JoinHouseholdRequest and its DTO representation.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface JoinRequestMapper {
  JoinRequestMapper INSTANCE = Mappers.getMapper(JoinRequestMapper.class);

  /**
   * Converts a JoinHouseholdRequest entity to a JoinHouseholdRequestResponse DTO.
   *
   * @param joinRequest the JoinHouseholdRequest entity to be converted
   * @return the corresponding JoinHouseholdRequestResponse DTO
   */
  JoinHouseholdRequestResponse joinRequestToResponse(JoinHouseholdRequest joinRequest);

  /**
   * Converts a list of JoinHouseholdRequest entities to a list of
   * JoinHouseholdRequestResponse DTOs.
   *
   * @param joinRequests the list of JoinHouseholdRequest entities to be converted
   * @return a list of corresponding JoinHouseholdRequestResponse DTOs
   */
  List<JoinHouseholdRequestResponse> joinRequestListToResponseList(
      List<JoinHouseholdRequest> joinRequests);
}