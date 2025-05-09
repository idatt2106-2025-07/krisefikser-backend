package com.group7.krisefikser.dto.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the response sent to the client containing general information.
 * It includes an ID, theme, title, and content.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralInfoResponse {
  String id;
  String theme;
  String title;
  String content;
}
