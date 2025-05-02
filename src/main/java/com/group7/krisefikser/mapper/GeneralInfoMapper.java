package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.model.GeneralInfo;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between GeneralInfoRequest and GeneralInfo objects.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
public interface GeneralInfoMapper {
  GeneralInfoMapper INSTANCE = Mappers.getMapper(GeneralInfoMapper.class);

  /**
   * Converts a GeneralInfoRequest object to a GeneralInfo object.
   * This method maps the fields of GeneralInfoRequest to the corresponding fields of GeneralInfo.
   *
   * @param generalInfoRequest the GeneralInfoRequest object to convert
   * @return the converted GeneralInfo object
   */
  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  GeneralInfo generalInfoRequestToGeneralInfo(GeneralInfoRequest generalInfoRequest);
}
