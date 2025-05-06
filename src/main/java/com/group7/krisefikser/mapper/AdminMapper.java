package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.response.AdminResponse;
import com.group7.krisefikser.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting User entities to SuperAdminResponse DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface AdminMapper {
  AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

  /**
   * Converts a User entity to a SuperAdminResponse DTO.
   * This method is used to map the properties of the User entity
   * to the corresponding properties of the SuperAdminResponse DTO.
   *
   * @param superAdmins the User entity to be converted
   * @return the converted SuperAdminResponse DTO
   */
  @Mapping(source = "email", target = "email")
  List<AdminResponse> userToAdminResponse(List<User> superAdmins);
}
