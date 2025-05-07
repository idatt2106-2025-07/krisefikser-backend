package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.AddNonUserMemberRequest;
import com.group7.krisefikser.dto.request.UpdateNonUserMemberRequest;
import com.group7.krisefikser.model.NonUserMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NonUserMemberMapper {
  NonUserMemberMapper INSTANCE = Mappers.getMapper(NonUserMemberMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  NonUserMember addNonUserMemberRequestToNonUserMember(
      AddNonUserMemberRequest addNonUserMemberRequest);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "id", target = "id")
  NonUserMember updateNonUserMemberRequestToNonUserMember(
      UpdateNonUserMemberRequest updateNonUserMemberRequest);
}
