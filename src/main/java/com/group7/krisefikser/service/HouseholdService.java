package com.group7.krisefikser.service;

import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.utils.UuidUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdService {

  private final HouseholdRepository householdRepo;

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
