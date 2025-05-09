package com.group7.krisefikser.dto.request.article;

import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for General Information requests.
 * This class is used to encapsulate the data
 * sent from the client to the server
 * when creating or updating general information.
 * It contains fields for the ID, theme, title, and content of the general information.
 */
@Data
public class GeneralInfoRequest {
  @NotBlank
  @ValidEnum(enumClass = Theme.class,
      message = "Theme must be one of the following (case-insensitive): {enumClass}")
  private String theme;
  @NotBlank(message = "Title is required")
  private String title;
  @NotBlank(message = "Content is required")
  private String content;
}
