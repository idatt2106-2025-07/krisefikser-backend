package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.Theme;
import lombok.Data;

@Data
public class GeneralInfoRequest {
  private Long id;
  private Theme theme;
  private String title;
  private String content;
}
