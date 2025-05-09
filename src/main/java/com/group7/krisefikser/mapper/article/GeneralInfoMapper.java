package com.group7.krisefikser.mapper.article;

import com.group7.krisefikser.dto.request.article.GeneralInfoRequest;
import com.group7.krisefikser.dto.response.article.GeneralInfoResponse;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.article.GeneralInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between GeneralInfoRequest and GeneralInfo objects.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper
public interface GeneralInfoMapper {
  GeneralInfoMapper INSTANCE = Mappers.getMapper(GeneralInfoMapper.class);

  /**
   * Custom mapper method to convert String to Theme enum.
   * This method handles the conversion of a string
   * to the corresponding Theme enum value.
   * If the string does not match any enum value,
   * it throws a RuntimeException.
   *
   * @param theme the theme string to convert
   * @return the corresponding Theme enum value
   */
  @Named("stringToTheme")
  default Theme stringToTheme(String theme) {
    try {
      return Theme.valueOf(theme.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new RuntimeException("Invalid theme: " + theme);
    }
  }

  /**
   * Converts a GeneralInfoRequest object to a GeneralInfo object.
   * This method maps the fields of GeneralInfoRequest to the corresponding fields of GeneralInfo.
   *
   * @param generalInfoRequest the GeneralInfoRequest object to convert
   * @return the converted GeneralInfo object
   */
  @Mapping(source = "theme", target = "theme", qualifiedByName = "stringToTheme")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  GeneralInfo requestToGeneralInfo(GeneralInfoRequest generalInfoRequest);

  /**
   * Converts a list of GeneralInfo objects to a list of GeneralInfoResponse objects.
   * This method maps the fields of GeneralInfo to the corresponding fields of GeneralInfoResponse.
   *
   * @param generalInfoList the list of GeneralInfo objects to convert
   * @return the list of converted GeneralInfoResponse objects
   */
  @Mapping(source = "id", target = "id")
  @Mapping(source = "theme", target = "theme")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  List<GeneralInfoResponse> generalInfoToResponseList(List<GeneralInfo> generalInfoList);

  /**
   * Converts a GeneralInfo object to a GeneralInfoResponse object.
   * This method maps the fields of GeneralInfo to the corresponding fields of GeneralInfoResponse.
   *
   * @param generalInfo the GeneralInfo object to convert
   * @return the converted GeneralInfoResponse object
   */
  GeneralInfoResponse generalInfoToResponse(GeneralInfo generalInfo);
}
