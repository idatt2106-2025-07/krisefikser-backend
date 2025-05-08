package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.model.NonUserMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between NonUserMember and its request DTOs.
 * This interface uses MapStruct to generate the implementation for mapping.
 */
@Mapper
public interface NonUserMemberMapper {
  NonUserMemberMapper INSTANCE = Mappers.getMapper(NonUserMemberMapper.class);

  /**
   * Converts a AddNonMemberRequest dto to NonUserMember model.
   *
   * @param addNonUserMemberRequest the AddNonUserMemberRequest object to convert
   * @return the converted NonUserMember object
   */
  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  NonUserMember addNonUserMemberRequestToNonUserMember(
      AddNonUserMemberRequest addNonUserMemberRequest);

  /**
   * Converts a UpdateNonUserMemberRequest dto to NonUserMember model.
   *
   * @param updateNonUserMemberRequest the UpdateNonUserMemberRequest object to convert
   * @return the converted NonUserMember object
   */
  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "id", target = "id")
  NonUserMember updateNonUserMemberRequestToNonUserMember(
      UpdateNonUserMemberRequest updateNonUserMemberRequest);
}
