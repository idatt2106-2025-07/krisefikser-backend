package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.SharePositionRequest;
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

  public void sharePosition(SharePositionRequest request) {
    UserPosition userPosition =
        UserPositionMapper.INSTANCE.sharePositionRequestToUserPosition(request);
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    userPosition.setUserId(Long.parseLong(userId));
    positionRepository.updateUserPosition(userPosition);
  }
}
