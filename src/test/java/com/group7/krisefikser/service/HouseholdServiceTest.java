package com.group7.krisefikser.service;

import com.group7.krisefikser.model.Household;
import com.group7.krisefikser.model.JoinHouseholdRequest;
import com.group7.krisefikser.repository.HouseholdRepository;
import com.group7.krisefikser.repository.JoinHouseholdRequestRepo;
import com.group7.krisefikser.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdServiceTest {
  @Mock
  private JoinHouseholdRequestRepo joinRequestRepo;
  @Mock
  private UserRepository userRepository;
  @Mock
  private HouseholdRepository householdRepository;

  @InjectMocks
  private HouseholdService householdService;

  @Test
  void createHousehold_shouldInsertHouseholdAndUpdateUser() {
    // Arrange
    Household household = new Household();
    household.setName("Test");
    household.setLongitude(10.0);
    household.setLatitude(60.0);
    Long userId = 1L;

    // Configure mock to return household with ID 5
    when(householdRepository.save(any(Household.class))).thenAnswer(invocation -> {
      Household savedHousehold = invocation.getArgument(0);
      savedHousehold.setId(5L);
      return savedHousehold;
    });

    // Act
    Household result = householdService.createHousehold(household, userId);

    // Assert
    assertEquals(5L, result.getId());
    verify(userRepository).updateUserHousehold(userId, 5L);
  }

  @Test
  void requestToJoin_shouldSaveJoinRequest() {
    Long householdId = 2L;
    Long userId = 3L;
    JoinHouseholdRequest savedRequest = new JoinHouseholdRequest();
    savedRequest.setHouseholdId(householdId);
    savedRequest.setUserId(userId);

    when(joinRequestRepo.save(any(JoinHouseholdRequest.class))).thenReturn(savedRequest);

    JoinHouseholdRequest result = householdService.requestToJoin(householdId, userId);

    assertEquals(householdId, result.getHouseholdId());
    assertEquals(userId, result.getUserId());
    verify(joinRequestRepo).save(any(JoinHouseholdRequest.class));
  }

  @Test
  void acceptJoinRequest_shouldUpdateUserAndDeleteRequest() {
    Long requestId = 1L;
    JoinHouseholdRequest request = new JoinHouseholdRequest();
    request.setHouseholdId(2L);
    request.setUserId(3L);

    when(joinRequestRepo.findById(requestId)).thenReturn(request);

    householdService.acceptJoinRequest(requestId);

    verify(userRepository).updateUserHousehold(3L, 2L);
    verify(joinRequestRepo).deleteById(requestId);
  }


  @Test
  void declineJoinRequest_shouldDeleteRequest() {
    Long requestId = 1L;

    householdService.declineJoinRequest(requestId);

    verify(joinRequestRepo).deleteById(requestId);
  }

  @Test
  void getRequestsForHousehold_shouldReturnRequestsList() {
    Long householdId = 1L;
    List<JoinHouseholdRequest> expected = List.of(new JoinHouseholdRequest(), new JoinHouseholdRequest());
    when(joinRequestRepo.findByHouseholdId(householdId)).thenReturn(expected);

    List<JoinHouseholdRequest> result = householdService.getRequestsForHousehold(householdId);

    assertEquals(expected, result);
    verify(joinRequestRepo).findByHouseholdId(householdId);
  }
}