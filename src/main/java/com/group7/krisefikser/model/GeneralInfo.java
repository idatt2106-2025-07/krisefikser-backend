package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralInfo {
  private Long id;
  private Theme theme;
  private String title;
  private String content;
}
