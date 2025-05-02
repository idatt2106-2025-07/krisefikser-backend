package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.RegisterRequest;
import com.group7.krisefikser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between RegisterRequest and User objects.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * Converts a RegisterRequest object to a User object.
   * This method maps the fields of RegisterRequest to the corresponding fields of User.
   *
   * @param registerRequest the RegisterRequest object to convert
   * @return the converted User object
   */
  @Mapping(source = "email", target = "email")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "password", target = "password")
  User registerRequestToUser(RegisterRequest registerRequest);
}
