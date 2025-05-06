package com.group7.krisefikser.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for searching storage items by name and type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageItemSearchRequest {
  private String searchTerm;
  private List<String> types;
  private String sortBy = "name";
  private String sortDirection = "asc";
}