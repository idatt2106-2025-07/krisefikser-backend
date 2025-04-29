package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(source = "email", target = "email")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "password", target = "password")
  User registerRequestToUser(RegisterRequest registerRequest);
}
