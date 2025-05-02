package com.group7.krisefikser.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for filtering items.
 * This class can be used to encapsulate any parameters needed for the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemFilterRequest {
  private List<String> types;
}