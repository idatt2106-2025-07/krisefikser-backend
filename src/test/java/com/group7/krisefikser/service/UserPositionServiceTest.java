package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.SharePositionRequest;
import com.group7.krisefikser.dto.response.HouseholdMemberPositionResponse;
import com.group7.krisefikser.model.UserPosition;
import com.group7.krisefikser.repository.UserPositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPositionServiceTest {

  @InjectMocks
  private UserPositionService userPositionService;

  @Mock
  private UserPositionRepository userPositionRepository;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @Captor
  private ArgumentCaptor<UserPosition> userPositionCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("42"); // Simulate authenticated user with ID 42
  }

  @Test
  void sharePosition_shouldAddNewPositionIfNotAlreadySharing() {
    SharePositionRequest request = new SharePositionRequest();
    request.setLatitude(10.0);
    request.setLongitude(20.0);

    when(userPositionRepository.isSharingPosition(42L)).thenReturn(false);

    userPositionService.sharePosition(request);

    verify(userPositionRepository).addUserPosition(userPositionCaptor.capture());
    assertEquals(42L, userPositionCaptor.getValue().getUserId());
  }

  @Test
  void sharePosition_shouldUpdatePositionIfAlreadySharing() {
    SharePositionRequest request = new SharePositionRequest();
    request.setLatitude(15.0);
    request.setLongitude(25.0);

    when(userPositionRepository.isSharingPosition(42L)).thenReturn(true);

    userPositionService.sharePosition(request);

    verify(userPositionRepository).updateUserPosition(userPositionCaptor.capture());
    assertEquals(42L, userPositionCaptor.getValue().getUserId());
  }

  @Test
  void isSharingPosition_shouldReturnTrueWhenSharing() {
    when(userPositionRepository.isSharingPosition(42L)).thenReturn(true);
    assertTrue(userPositionService.isSharingPosition());
  }

  @Test
  void isSharingPosition_shouldReturnFalseWhenNotSharing() {
    when(userPositionRepository.isSharingPosition(42L)).thenReturn(false);
    assertFalse(userPositionService.isSharingPosition());
  }

  @Test
  void getHouseholdPositions_shouldReturnMappedResponses() {
    UserPosition[] userPositions = new UserPosition[1];
    userPositions[0] = new UserPosition();
    userPositions[0].setLatitude(10.0);
    userPositions[0].setLongitude(20.0);
    userPositions[0].setUserId(42L);

    when(userPositionRepository.getHouseholdPositions(42L)).thenReturn(userPositions);

    HouseholdMemberPositionResponse[] responses = userPositionService.getHouseholdPositions();
    assertNotNull(responses);
    assertEquals(1, responses.length);
    assertEquals(10.0, responses[0].getLatitude());
  }

  @Test
  void deleteUserPosition_shouldCallRepository() {
    userPositionService.deleteUserPosition();
    verify(userPositionRepository).deleteUserPosition(42L);
  }
}
