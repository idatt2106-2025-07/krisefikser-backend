package com.group7.krisefikser.dto.request;

import com.group7.krisefikser.enums.EmailTemplateType;
import lombok.Data;

import java.util.Map;

@Data
public class EmailTemplateRequest {
  private String to;
  private EmailTemplateType type;
  private Map<String, String> params;
}
