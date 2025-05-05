package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.HouseholdInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the HouseholdInvitationRepository.
 * This class is used to test the HouseholdInvitationRepository functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
class HouseholdInvitationRepositoryTest {
  @Autowired
  private HouseholdInvitationRepository invitationRepository;

  private HouseholdInvitation testInvitation1;
  private HouseholdInvitation testInvitation2;

  @BeforeEach
  void setUp() {
    testInvitation1 = new HouseholdInvitation();
    testInvitation1.setHouseholdId(1L);
    testInvitation1.setInvitedByUserId(1L);
    testInvitation1.setInvitedEmail("test1@example.com");

    testInvitation2 = new HouseholdInvitation();
    testInvitation2.setHouseholdId(2L);
    testInvitation2.setInvitedByUserId(2L);
    testInvitation2.setInvitedEmail("test2@example.com");
  }

  @Test
  @Rollback
  void save_shouldInsertNewInvitationAndAssignId() {
    HouseholdInvitation savedInvitation = invitationRepository.save(testInvitation1);

    assertNotNull(savedInvitation.getId(), "ID should be assigned after insertion");
    assertTrue(savedInvitation.getId() > 0, "ID should be greater than 0");
    assertNotNull(savedInvitation.getInvitationToken(), "Token should be generated");
    assertNotNull(savedInvitation.getCreatedAt(), "Created timestamp should be set");
    assertEquals(testInvitation1.getHouseholdId(), savedInvitation.getHouseholdId());
    assertEquals(testInvitation1.getInvitedByUserId(), savedInvitation.getInvitedByUserId());
    assertEquals(testInvitation1.getInvitedEmail(), savedInvitation.getInvitedEmail());
  }

  @Test
  @Rollback
  void findByToken_shouldReturnInvitationWithMatchingToken() {
    HouseholdInvitation savedInvitation = invitationRepository.save(testInvitation1);
    String token = savedInvitation.getInvitationToken();

    Optional<HouseholdInvitation> foundInvitation = invitationRepository.findByToken(token);

    assertTrue(foundInvitation.isPresent(), "Invitation should be found by token");
    assertEquals(savedInvitation.getId(), foundInvitation.get().getId());
    assertEquals(savedInvitation.getHouseholdId(), foundInvitation.get().getHouseholdId());
    assertEquals(savedInvitation.getInvitedByUserId(), foundInvitation.get().getInvitedByUserId());
    assertEquals(savedInvitation.getInvitedEmail(), foundInvitation.get().getInvitedEmail());
    assertEquals(token, foundInvitation.get().getInvitationToken());
  }

  @Test
  @Rollback
  void findByEmail_shouldReturnAllInvitationsWithMatchingEmail() {
    String sharedEmail = "shared@example.com";
    testInvitation1.setInvitedEmail(sharedEmail);
    testInvitation2.setInvitedEmail(sharedEmail);

    invitationRepository.save(testInvitation1);
    invitationRepository.save(testInvitation2);

    List<HouseholdInvitation> invitations = invitationRepository.findByEmail(sharedEmail);

    assertEquals(2, invitations.size(), "Should find 2 invitations with the same email");
    assertTrue(invitations.stream()
      .anyMatch(inv -> inv.getHouseholdId().equals(testInvitation1.getHouseholdId())));
    assertTrue(invitations.stream()
      .anyMatch(inv -> inv.getHouseholdId().equals(testInvitation2.getHouseholdId())));
  }
}