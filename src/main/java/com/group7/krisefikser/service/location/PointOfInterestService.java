package com.group7.krisefikser.service.location;

import com.group7.krisefikser.dto.request.location.GetPointsOfInterestRequest;
import com.group7.krisefikser.dto.request.location.PointOfInterestRequest;
import com.group7.krisefikser.dto.response.location.PointOfInterestResponse;
import com.group7.krisefikser.enums.PointOfInterestType;
import com.group7.krisefikser.model.location.PointOfInterest;
import com.group7.krisefikser.repository.location.PointOfInterestRepo;
import com.group7.krisefikser.utils.JwtUtils;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing points of interest.
 * This class will contain methods to handle business logic related to points of interest.
 * It will interact with the repository layer to perform CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class PointOfInterestService {
  private final PointOfInterestRepo pointOfInterestRepo;
  private final JwtUtils jwtUtils;

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
                    point.getType().name(),
                    point.getOpensAt() != null ? point.getOpensAt().toString() : null,
                    point.getClosesAt() != null ? point.getClosesAt().toString() : null,
                    point.getContactNumber(),
                    point.getDescription()
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

  /**
   * Method to add a new point of interest.
   * This method will interact with the repository to add a new point of
   * interest to the database.
   *
   * @param pointOfInterestRequest The point of interest to be added.
   * @return The added point of interest.
   */
  @Transactional(rollbackFor = Exception.class)
  public PointOfInterestResponse addPointOfInterest(PointOfInterestRequest
                                                              pointOfInterestRequest) {
    PointOfInterest point = new PointOfInterest(
            null,
            pointOfInterestRequest.getLatitude(),
            pointOfInterestRequest.getLongitude(),
            PointOfInterestType.fromString(pointOfInterestRequest.getType()),
            pointOfInterestRequest.getOpensAt() != null
                    ? LocalTime.parse(pointOfInterestRequest.getOpensAt())
                    : null,
            pointOfInterestRequest.getClosesAt() != null
                    ? LocalTime.parse(pointOfInterestRequest.getClosesAt())
                    : null,
            pointOfInterestRequest.getContactNumber(),
            pointOfInterestRequest.getDescription()
    );

    pointOfInterestRepo.addPointOfInterest(point);

    if (point.getId() == null) {
      throw new IllegalStateException("Failed to add point of interest");
    }
    return new PointOfInterestResponse(
            point.getId(),
            point.getLatitude(),
            point.getLongitude(),
            point.getType().name(),
            point.getOpensAt() != null ? point.getOpensAt().toString() : null,
            point.getClosesAt() != null ? point.getClosesAt().toString() : null,
            point.getContactNumber(),
            point.getDescription()
    );
  }

  /**
   * Method do delete a point of interest.
   * This method will interact with the repository to delete a point of
   * interest from the database.
   */
  @Transactional(rollbackFor = Exception.class)
  public void deletePointOfInterest(Long id) {
    int rowsAffected = pointOfInterestRepo.deletePointOfInterest(id);

    if (rowsAffected == 0) {
      throw new IllegalArgumentException("Point of interest not found, no rows affected");
    }
    if (rowsAffected > 1) {
      throw new IllegalStateException("Multiple rows affected when deleting point of interest");
    }
  }

  /**
   * Method to update a point of interest.
   * This method will interact with the repository to update an existing point
   * of interest in the database.
   *
   * @param pointOfInterestRequest The point of interest to be updated.
   * @return The updated point of interest.
   */
  @Transactional(rollbackFor = Exception.class)
  public PointOfInterestResponse updatePointOfInterest(
          Long id, PointOfInterestRequest pointOfInterestRequest) {

    PointOfInterest point = new PointOfInterest(
            id,
            pointOfInterestRequest.getLatitude(),
            pointOfInterestRequest.getLongitude(),
            PointOfInterestType.fromString(pointOfInterestRequest.getType()),
            pointOfInterestRequest.getOpensAt() != null
                    ? LocalTime.parse(pointOfInterestRequest.getOpensAt())
                    : null,
            pointOfInterestRequest.getClosesAt() != null
                    ? LocalTime.parse(pointOfInterestRequest.getClosesAt())
                    : null,
            pointOfInterestRequest.getContactNumber(),
            pointOfInterestRequest.getDescription()
    );

    int rowsAffected = pointOfInterestRepo.updatePointOfInterest(point);

    if (rowsAffected == 0) {
      throw new IllegalArgumentException("Point of interest not found, no rows affected");
    }
    if (rowsAffected > 1) {
      throw new IllegalStateException("Multiple rows affected when updating point of interest");
    }

    return new PointOfInterestResponse(
            point.getId(),
            point.getLatitude(),
            point.getLongitude(),
            point.getType().name(),
            point.getOpensAt() != null ? point.getOpensAt().toString() : null,
            point.getClosesAt() != null ? point.getClosesAt().toString() : null,
            point.getContactNumber(),
            point.getDescription()
    );
  }
}
