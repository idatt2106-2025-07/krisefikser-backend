package com.group7.krisefikser.mapper.household;

import com.group7.krisefikser.dto.request.household.HouseholdRequest;
import com.group7.krisefikser.dto.response.household.HouseholdResponse;
import com.group7.krisefikser.model.household.Household;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Household and its DTO representations.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface HouseholdMapper {
  HouseholdMapper INSTANCE = Mappers.getMapper(HouseholdMapper.class);

  /**
   * Converts a HouseholdRequest DTO to a Household entity.
   *
   * @param householdRequest the HouseholdRequest DTO
   * @return the corresponding Household entity
   */
  @Mapping(target = "id", ignore = true)
  Household householdRequestToHousehold(HouseholdRequest householdRequest);

  /**
   * Converts a Household entity to a HouseholdResponse DTO.
   *
   * @param household the Household entity
   * @return the corresponding HouseholdResponse DTO
   */
  HouseholdResponse householdToHouseholdResponse(Household household);
}