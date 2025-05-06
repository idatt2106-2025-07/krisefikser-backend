package com.group7.krisefikser.repository;

import com.group7.krisefikser.model.Household;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HouseholdRepositoryTest {

  @Autowired
  private HouseholdRepository householdRepository;

  @Test
  void createHousehold_validInput_returnsGeneratedIdAndPersists() {
    String name = "Test Household";
    double longitude = 12.34;
    double latitude = 56.78;

    Long id = householdRepository.createHousehold(name, longitude, latitude);

    assertNotNull(id);
    assertTrue(id > 0);
    assertTrue(householdRepository.existsByName(name));
  }

  @Test
  void existsByName_existingHousehold_returnsTrue() {
    // "The Smiths" exists in testdata
    assertTrue(householdRepository.existsByName("The Smiths"));
  }

  @Test
  void existsByName_nonExistingHousehold_returnsFalse() {
    assertFalse(householdRepository.existsByName("Nonexistent Household"));
  }

  @Test
  void getHouseholdByName_existingHousehold_returnsHousehold() {
    Optional<Household> householdOptional = householdRepository.getHouseholdByName("The Smiths");
    assertTrue(householdOptional.isPresent());
    Household household = householdOptional.get();
    assertEquals("The Smiths", household.getName());
    assertEquals(1L, household.getId());
    assertEquals(10.75, household.getLongitude());
    assertEquals(59.91, household.getLatitude());
  }

  @Test
  void getHouseholdByName_nonExistingHousehold_throwsException() {
    String name = "Nonexistent Household";
    Optional<Household> householdOptional = householdRepository.getHouseholdByName(name);
    assertFalse(householdOptional.isPresent());
  }

  @Test
  void getHouseholdById_existingHousehold_returnsHousehold() {
    Optional<Household> householdOptional = householdRepository.getHouseholdById(1L);
    assertTrue(householdOptional.isPresent());
    Household household = householdOptional.get();
    assertEquals("The Smiths", household.getName());
    assertEquals(1L, household.getId());
    assertEquals(10.75, household.getLongitude());
    assertEquals(59.91, household.getLatitude());
  }

  @Test
  void getHouseholdById_nonExistingHousehold_returnsEmpty() {
    Optional<Household> householdOptional = householdRepository.getHouseholdById(999L);
    assertFalse(householdOptional.isPresent());
  }

  @Test
  void addHouseholdToGroup_validInput_updatesHousehold() {
    long householdId = 1L;
    long groupId = 2L;

    householdRepository.addHouseholdToGroup(householdId, groupId);

    Optional<Household> householdOptional = householdRepository.getHouseholdById(householdId);
    assertTrue(householdOptional.isPresent());
    Household household = householdOptional.get();
    assertEquals(groupId, household.getEmergencyGroupId());
  }
}