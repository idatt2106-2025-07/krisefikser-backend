package com.group7.krisefikser.mapper;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.model.GeneralInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface GeneralInfoMapper {
  GeneralInfoMapper INSTANCE = Mappers.getMapper(GeneralInfoMapper.class);

  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  GeneralInfo generalInfoRequestToGeneralInfo(GeneralInfoRequest generalInfoRequest);
}
