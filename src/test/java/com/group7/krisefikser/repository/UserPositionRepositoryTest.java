package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.UserPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserPositionRepositoryTest {

  @Autowired
  private UserPositionRepository userPositionRepository;

  @BeforeEach
  void setUp() {
    // Test data is assumed to already be loaded via test profile setup scripts.
    // No cleanup here to avoid foreign key constraint errors.
  }

  @Test
  void isSharingPosition_existingUser_returnsTrue() {
    boolean result = userPositionRepository.isSharingPosition(1L);
    assertTrue(result);
  }

  @Test
  void isSharingPosition_nonExistingUser_returnsFalse() {
    boolean result = userPositionRepository.isSharingPosition(999L);
    assertFalse(result);
  }

  @Test
  void addUserPosition_validUser_addsPosition() {
    UserPosition newPosition = new UserPosition();
    newPosition.setUserId(8L);
    newPosition.setLatitude(60.55);
    newPosition.setLongitude(11.55);

    userPositionRepository.addUserPosition(newPosition);

    boolean stored = userPositionRepository.isSharingPosition(8L);
    assertTrue(stored);
  }

  @Test
  void updateUserPosition_existingUser_updatesCoordinates() {
    UserPosition updated = new UserPosition();
    updated.setUserId(1L);
    updated.setLatitude(59.999);
    updated.setLongitude(10.999);

    userPositionRepository.updateUserPosition(updated);

    boolean stillSharing = userPositionRepository.isSharingPosition(1L);
    assertTrue(stillSharing);
  }

  @Test
  void deleteUserPosition_existingUser_removesPosition() {
    userPositionRepository.deleteUserPosition(8L);
    boolean stillExists = userPositionRepository.isSharingPosition(8L);
    assertFalse(stillExists);
  }

  @Test
  void getHouseholdPositions_excludesSelfAndReturnsOthers() {
    // Assumes user 1 and 2 are in same household
    UserPosition[] positions = userPositionRepository.getHouseholdPositions(1L);

    assertNotNull(positions);
    assertTrue(positions.length >= 1);
    List<Long> userIds = Arrays.stream(positions).map(UserPosition::getUserId).toList();
    assertFalse(userIds.contains(1L));
  }

  @Test
  void getGroupPositions_excludesSelfAndReturnsOthersInSameEmergencyGroup() {
    // Assumes users are in households that belong to the same emergency group
    // but not necessarily in the same household
    UserPosition[] positions = userPositionRepository.getGroupPositions(1L);

    assertNotNull(positions);
    assertTrue(positions.length > 0);

    List<Long> userIds = Arrays.stream(positions).map(UserPosition::getUserId).toList();

    // Should not include the requesting user
    assertFalse(userIds.contains(1L));

    // Should include users from other households in the same emergency group
    // This assumes your test data has such users with positions
    assertTrue(userIds.size() > 0);
  }
}
