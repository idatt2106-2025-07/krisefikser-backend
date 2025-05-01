package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.AffectedAreaRequest;
import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.mapper.AffectedAreaMapper;
import com.group7.krisefikser.model.AffectedArea;
import com.group7.krisefikser.repository.AffectedAreaRepo;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service class for handling operations related to affected areas.
 * The role of this service is to manage the business logic related to
 * affected areas, such as retrieving and updating.
 */
@Service
@RequiredArgsConstructor
public class AffectedAreaService {
  private final AffectedAreaRepo affectedAreaRepo;

  /**
   * Retrieves all affected areas from the repository and maps them to AffectedAreaResponse
   * objects.
   *
   * @return a list of AffectedAreaResponse objects containing details of all
   * affected areas.
   */
  public List<AffectedAreaResponse> getAllAffectedAreas() {
    return affectedAreaRepo.getAllAffectedAreas()
            .stream()
            .map(area -> new AffectedAreaResponse(
                    area.getId(),
                    area.getLongitude(),
                    area.getLatitude(),
                    area.getHighDangerRadiusKm(),
                    area.getMediumDangerRadiusKm(),
                    area.getLowDangerRadiusKm(),
                    area.getSeverityLevel(),
                    area.getDescription(),
                    area.getStartDate().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME)))
            .toList();
  }

  /**
   * Adds a new affected area to the repository.
   *
   * @param affectedAreaRequest the request object containing details of the affected area
   *                            to be added.
   * @return the response object containing details of the added affected area.
   */
  @Transactional
  public AffectedAreaResponse addAffectedArea(AffectedAreaRequest affectedAreaRequest) {
    AffectedArea area = AffectedAreaMapper.INSTANCE.requestToAffectedArea(affectedAreaRequest);
    affectedAreaRepo.addAffectedArea(area);

    if (area.getId() != null) {
      return AffectedAreaMapper.INSTANCE.affectedAreaToResponse(area);
    } else {
      throw new IllegalStateException("Failed to add affected area");
    }
  }

  /**
   * Deletes an affected area from the repository.
   *
   * @param id the ID of the affected area to be deleted.
   */
  @Transactional
  public void deleteAffectedArea(long id) {
    int rowsAffected = affectedAreaRepo.deleteAffectedArea(id);

    if (rowsAffected == 0) {
      throw new IllegalArgumentException("Failed to delete affected area");
    }
    if (rowsAffected > 1) {
      throw new IllegalStateException("Multiple rows deleted, check database integrity");
    }
  }

  /**
   * Updates an existing affected area in the repository.
   *
   * @param affectedAreaRequest the request object containing updated details of the affected
   *                            area.
   * @return the response object containing details of the updated affected area.
   */
  @Transactional
  public AffectedAreaResponse updateAffectedArea(long id, AffectedAreaRequest affectedAreaRequest) {
    AffectedArea area = AffectedAreaMapper.INSTANCE.requestToAffectedArea(affectedAreaRequest);
    area.setId(id);
    int rowsAffected = affectedAreaRepo.updateAffectedArea(area);

    if (rowsAffected == 0) {
      throw new IllegalStateException("Failed to update affected area");
    }
    return AffectedAreaMapper.INSTANCE.affectedAreaToResponse(area);
  }
}
