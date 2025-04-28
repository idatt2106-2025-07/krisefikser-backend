package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.response.AffectedAreaResponse;
import com.group7.krisefikser.repository.AffectedAreaRepo;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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
                    area.getDangerRadiusKm(),
                    area.getSeverityLevel(),
                    area.getDescription(),
                    area.getStartDate()))
                    .toList();
  }
}
