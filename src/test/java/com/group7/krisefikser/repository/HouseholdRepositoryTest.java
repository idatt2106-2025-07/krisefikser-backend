package com.group7.krisefikser.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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
}