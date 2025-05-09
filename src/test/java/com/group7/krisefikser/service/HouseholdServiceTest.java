package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.JoinHouseholdRequest;
import com.group7.krisefikser.dto.response.ReadinessResponse;
import com.group7.krisefikser.enums.ItemType;
import com.group7.krisefikser.model.*;
import com.group7.krisefikser.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

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
  @Mock
  private NonUserMemberRepository nonUserMemberRepository;
  @Mock
  private StorageItemRepo storageItemRepo;
  @Mock
  private ItemRepo itemRepo;

  @InjectMocks
  private HouseholdService householdService;

  @BeforeEach
  void setUp() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, List.of());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

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

  @Test
  void getHouseholdById_shouldReturnHousehold() {
    Long householdId = 1L;
    Household expected = new Household();
    expected.setId(householdId);
    expected.setName("Test Household");

    when(householdRepository.getHouseholdById(householdId)).thenReturn(Optional.of(expected));

    Household result = householdService.getHouseholdById(householdId);

    assertEquals(expected, result);
    verify(householdRepository).getHouseholdById(householdId);
  }

  @Test
  void testCalculateReadiness_NormalCase() {
    User user = new User();
    user.setId(1L);
    user.setHouseholdId(100L);

    Household household = new Household();
    household.setId(100L);

    StorageItem si = new StorageItem();
    si.setItemId(10);
    si.setQuantity(2);
    si.setExpirationDate(LocalDateTime.now().plusDays(10));

    Item item = new Item();
    item.setCalories(500);
    item.setUnit("L");
    item.setType(ItemType.valueOf("DRINK"));

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(householdRepository.getHouseholdById(100L)).thenReturn(Optional.of(household));
    when(userRepository.getUsersByHouseholdId(100L)).thenReturn(List.of(user));
    when(nonUserMemberRepository.getNonUserMembersByHousehold(100L)).thenReturn(List.of());
    when(storageItemRepo.getAllStorageItems(100)).thenReturn(List.of(si));
    when(itemRepo.findById(10)).thenReturn(Optional.of(item));

    ReadinessResponse response = householdService.calculateReadinessForHousehold();

    assertNotNull(response);
    assertEquals(0, response.getDays());
    assertTrue(response.getHours() > 0);
  }

  @Test
  void testCalculateReadiness_UserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    ReadinessResponse response = householdService.calculateReadinessForHousehold();

    assertNull(response);
  }

  @Test
  void testCalculateReadiness_HouseholdNotFound() {
    User user = new User();
    user.setId(1L);
    user.setHouseholdId(999L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(householdRepository.getHouseholdById(999L)).thenReturn(Optional.empty());

    ReadinessResponse response = householdService.calculateReadinessForHousehold();

    assertNull(response);
  }

  @Test
  void testCalculateReadiness_ExpiredItemsIgnored() {
    User user = new User();
    user.setId(1L);
    user.setHouseholdId(100L);

    Household household = new Household();
    household.setId(100L);

    StorageItem expiredItem = new StorageItem();
    expiredItem.setItemId(10);
    expiredItem.setQuantity(5);
    expiredItem.setExpirationDate(LocalDateTime.now().minusDays(1)); // expired

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(householdRepository.getHouseholdById(100L)).thenReturn(Optional.of(household));
    when(userRepository.getUsersByHouseholdId(100L)).thenReturn(List.of(user));
    when(nonUserMemberRepository.getNonUserMembersByHousehold(100L)).thenReturn(List.of());
    when(storageItemRepo.getAllStorageItems(100)).thenReturn(List.of(expiredItem));

    ReadinessResponse response = householdService.calculateReadinessForHousehold();

    assertNotNull(response);
    assertEquals(0, response.getDays());
    assertEquals(0, response.getHours()); // no valid items
  }

  @Test
  void testCalculateReadiness_EmptyInventory() {
    User user = new User();
    user.setId(1L);
    user.setHouseholdId(100L);

    Household household = new Household();
    household.setId(100L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(householdRepository.getHouseholdById(100L)).thenReturn(Optional.of(household));
    when(userRepository.getUsersByHouseholdId(100L)).thenReturn(List.of(user));
    when(nonUserMemberRepository.getNonUserMembersByHousehold(100L)).thenReturn(List.of());
    when(storageItemRepo.getAllStorageItems(100)).thenReturn(List.of());

    ReadinessResponse response = householdService.calculateReadinessForHousehold();

    assertNotNull(response);
    assertEquals(0, response.getDays());
    assertEquals(0, response.getHours());
  }
}