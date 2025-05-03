package com.group7.krisefikser.service;

import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.utils.UuidUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling household-related operations.
 * This class provides methods for creating and managing households.
 */
@Service
@RequiredArgsConstructor
public class HouseholdService {

  private final HouseholdRepository householdRepo;

  /**
   * Creates a household for the user with a unique name.
   * The household name is generated based on the user's name and a UUID.
   * If a household with the same name already exists, it appends a counter to the name.
   *
   * @param userName The name of the user for whom the household is being created.
   * @return The ID of the created household.
   */
  public Long createHouseholdForUser(String userName) {
    Long householdId;
    int counter = 1;
    String baseName = userName + "'s household"
        + UuidUtils.generateShortenedUuid();
    String householdName = baseName;

    while (householdRepo.existsByName(householdName)) {
      counter++;
      householdName = baseName + " (" + counter + ")";
    }
    // Right now we are creating a household with default values for longitude and latitude
    // In the future, we might want to get these values from the user or use a geolocation service
    double longitude = 0.0;
    double latitude = 0.0;
    return householdRepo.createHousehold(householdName, longitude, latitude);
  }
}
