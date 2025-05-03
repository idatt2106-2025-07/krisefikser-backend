package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.Theme;
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
  private String theme;
  private String title;
  private String content;
}
