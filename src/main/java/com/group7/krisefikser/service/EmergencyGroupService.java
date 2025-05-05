package com.group7.krisefikser.service;


import com.group7.krisefikser.dto.request.EmergencyGroupRequest;
import com.group7.krisefikser.dto.response.EmergencyGroupResponse;
import com.group7.krisefikser.mapper.EmergencyGroupMapper;
import com.group7.krisefikser.model.EmergencyGroup;
import com.group7.krisefikser.repository.EmergencyGroupRepo;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to emergency groups.
 * The role of this service is to manage the business logic related to
 * emergency groups, such as retrieving and adding.
 */
@Service
@RequiredArgsConstructor
public class EmergencyGroupService {
  private final EmergencyGroupRepo emergencyGroupRepo;

  /**
   * Retrieves the EmergencyGroup object with the specified ID from the repository.
   *
   * @param id the ID of the EmergencyGroup to retrieve
   * @return the EmergencyGroupResponse object with the specified ID
   */
  public EmergencyGroupResponse getEmergencyGroupById(Long id) {
    try {
      EmergencyGroup group = emergencyGroupRepo.getEmergencyGroupById(id);
      return EmergencyGroupMapper.INSTANCE.emergencyGroupToResponse(group);
    } catch (EmptyResultDataAccessException e) {
      throw new NoSuchElementException("Emergency group with ID " + id + " not found.");
    }
  }

  /**
   * Adds a new EmergencyGroup to the repository.
   *
   * @param request the EmergencyGroup object to add
   * @return the EmergencyGroupResponse object representing the added group
   */
  public EmergencyGroupResponse addEmergencyGroup(EmergencyGroupRequest request) {
    try {
      EmergencyGroup group = EmergencyGroupMapper.INSTANCE
              .emergencyGroupRequestToEntity(request);
      emergencyGroupRepo.addEmergencyGroup(group);
      return EmergencyGroupMapper.INSTANCE.emergencyGroupToResponse(group);
    } catch (DataIntegrityViolationException  e) {
      throw new IllegalArgumentException("Failed to add emergency group. Name already taken.");
    }
  }
}
