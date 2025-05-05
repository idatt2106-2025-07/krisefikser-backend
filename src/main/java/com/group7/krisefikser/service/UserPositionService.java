package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.mapper.UserPositionMapper;
import com.group7.krisefikser.model.UserPosition;
import com.group7.krisefikser.repository.UserPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPositionService {

  private final UserPositionRepository positionRepository;
  private final UserPositionRepository userPositionRepository;

  public void sharePosition(SharePositionRequest request) {
    UserPosition userPosition =
        UserPositionMapper.INSTANCE.sharePositionRequestToUserPosition(request);
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    userPosition.setUserId(Long.parseLong(userId));

    boolean isSharingPosition = positionRepository.isSharingPosition(userPosition.getUserId());

    if (!isSharingPosition) {
      userPositionRepository.addUserPosition(userPosition);
    } else {
      userPositionRepository.updateUserPosition(userPosition);
    }
  }

  public boolean isSharingPosition() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return positionRepository.isSharingPosition(Long.parseLong(userId));
  }

  public HouseholdMemberPositionResponse[] getHouseholdPositions() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    UserPosition[] userPositions = positionRepository.getHouseholdPositions(Long.parseLong(userId));
    return UserPositionMapper.INSTANCE.userPositionArrayToHouseholdMemberPositionResponseArray(
            userPositions);
  }
}
