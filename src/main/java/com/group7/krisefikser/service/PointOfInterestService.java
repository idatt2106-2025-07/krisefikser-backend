package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.response.PointOfInterestResponse;
import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.repository.PointOfInterestRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing points of interest.
 * This class will contain methods to handle business logic related to points of interest.
 * It will interact with the repository layer to perform CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class PointOfInterestService {
  private final PointOfInterestRepo pointOfInterestRepo;

  /**
   * Method to get all points of interest.
   * This method will interact with the repository to fetch all points of
   * interest from the database.
   *
   * @return List of all points of interest
   */
  public List<PointOfInterestResponse> getPointsOfInterestByTypes(
          GetPointsOfInterestRequest request) {
    List<PointOfInterestType> types;
    try {
      types = mapStringsToPointOfInterestTypes(request.getTypes());

    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid point of interest type provided", e);
    }

    return pointOfInterestRepo.getPointsOfInterestByTypes(types)
            .stream()
            .map(point -> new PointOfInterestResponse(
                    point.getId(),
                    point.getLatitude(),
                    point.getLongitude(),
                    point.getType().name()
            ))
            .toList();

  }

  /**
   * Helper method to map strings to PointOfInterestType enums.
   *
   * @param types List of strings representing point of interest types
   * @return List of PointOfInterestType enums
   */
  private List<PointOfInterestType> mapStringsToPointOfInterestTypes(List<String> types) {
    if (types == null || types.isEmpty()) {
      return List.of();
    }

    return types.stream()
            .map(PointOfInterestType::fromString)
            .toList();
  }
}
