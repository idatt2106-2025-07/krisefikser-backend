package com.group7.krisefikser.repository;

import com.group7.krisefikser.dto.request.household.JoinHouseholdRequest;
import com.group7.krisefikser.repository.household.JoinHouseholdRequestRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the JoinHouseholdRequestRepo.
 * This class is used to test the JoinHouseholdRequestRepo functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
class JoinHouseholdRequestRepoTest {
  @Autowired
  private JoinHouseholdRequestRepo joinHouseholdRequestRepo;

  /**
   * Test method to verify finding join household requests by household ID.
   * It checks if the correct requests are returned for a specific household ID.
   */
  @Test
  void findByHouseholdId() {
    List<JoinHouseholdRequest> requests = joinHouseholdRequestRepo.findByHouseholdId(1L);

    assertNotNull(requests);
    assertEquals(1, requests.size());
    assertEquals(3L, requests.getFirst().getUserId());
    assertEquals(1L, requests.getFirst().getHouseholdId());
  }

  /**
   * Test method to verify finding a join household request by its ID.
   * It checks if the retrieved request has the expected values.
   */
  @Test
  void findById() {
    JoinHouseholdRequest request = joinHouseholdRequestRepo.findById(1L);

    assertNotNull(request);
    assertEquals(2L, request.getUserId());
    assertEquals(2L, request.getHouseholdId());
  }

  /**
   * Test method to verify saving a new join household request.
   * It creates a new request, saves it, and verifies it was assigned an ID.
   */
  @Test
  void save() {
    JoinHouseholdRequest newRequest = new JoinHouseholdRequest(null, 5L, 4L);
    JoinHouseholdRequest savedRequest = joinHouseholdRequestRepo.save(newRequest);

    assertNotNull(savedRequest);
    assertNotNull(savedRequest.getId());
    assertEquals(5L, savedRequest.getHouseholdId());
    assertEquals(4L, savedRequest.getUserId());

    // Clean up
    joinHouseholdRequestRepo.deleteById(savedRequest.getId());
  }

  /**
   * Test method to verify deleting a join household request.
   * It creates a new request, saves it, deletes it, and then verifies it no longer exists.
   */
  @Test
  void deleteById() {
    // Create a request to delete
    JoinHouseholdRequest newRequest = new JoinHouseholdRequest(null, 3L, 5L);
    JoinHouseholdRequest savedRequest = joinHouseholdRequestRepo.save(newRequest);
    Long id = savedRequest.getId();

    // Delete the request
    joinHouseholdRequestRepo.deleteById(id);

    // Verify it no longer exists
    assertThrows(EmptyResultDataAccessException.class, () -> joinHouseholdRequestRepo.findById(id));
  }
}