package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.response.SuperAdminResponse;
import com.group7.krisefikser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SuperAdminMapper {
  SuperAdminMapper INSTANCE = Mappers.getMapper(SuperAdminMapper.class);

  @Mapping(source = "email", target = "email")
  List<SuperAdminResponse> userToSuperAdminResponse(List<User> superAdmins);
}
