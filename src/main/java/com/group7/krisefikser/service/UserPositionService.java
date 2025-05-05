package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.mapper.UserPositionMapper;
import com.group7.krisefikser.model.UserPosition;
import com.group7.krisefikser.repository.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user positions.
 * Provides methods to share, check, retrieve, and delete user positions.
 */
@Service
@RequiredArgsConstructor
public class UserPositionService {

  private final UserPositionRepository userPositionRepository;

  /**
   * Shares the user's position.
   * If the user is already sharing a position, it updates the existing position.
   * Otherwise, it adds a new position.
   *
   * @param request The request containing the user's position details.
   */
  public void sharePosition(SharePositionRequest request) {
    UserPosition userPosition =
        UserPositionMapper.INSTANCE.sharePositionRequestToUserPosition(request);
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    userPosition.setUserId(Long.parseLong(userId));

    boolean isSharingPosition = userPositionRepository.isSharingPosition(userPosition.getUserId());

    if (!isSharingPosition) {
      userPositionRepository.addUserPosition(userPosition);
    } else {
      userPositionRepository.updateUserPosition(userPosition);
    }
  }

  /**
   * Checks if the user is sharing their position.
   *
   * @return true if the user is sharing their position, false otherwise.
   */
  public boolean isSharingPosition() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return userPositionRepository.isSharingPosition(Long.parseLong(userId));
  }

  /**
   * Retrieves the household positions of the user.
   *
   * @return An array of HouseholdMemberPositionResponse
   *        containing the positions of household members.
   */
  public HouseholdMemberPositionResponse[] getHouseholdPositions() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    UserPosition[] userPositions = userPositionRepository
        .getHouseholdPositions(Long.parseLong(userId));
    return UserPositionMapper.INSTANCE.userPositionArrayToHouseholdMemberPositionResponseArray(
            userPositions);
  }

  /**
   * Deletes the user's position.
   * This stops sharing the user's position.
   */
  public void deleteUserPosition() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    userPositionRepository.deleteUserPosition(Long.parseLong(userId));
  }
}
