package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
  @Test
  void noArgsConstructor_shouldCreateEmptyUser() {
    User user = new User();

    assertNull(user.getId());
    assertNull(user.getEmail());
    assertNull(user.getName());
    assertNull(user.getHouseholdId());
    assertNull(user.getPassword());
    assertNull(user.getRole());
  }

  @Test
  void allArgsConstructor_shouldSetAllFields() {
    User user = new User(
        1L,
        "test@example.com",
        "Test User",
        10L,
        "securepassword",
        Role.ROLE_NORMAL,
        false
    );

    assertEquals(1L, user.getId());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("Test User", user.getName());
    assertEquals(10L, user.getHouseholdId());
    assertEquals("securepassword", user.getPassword());
    assertEquals(Role.ROLE_NORMAL, user.getRole());
  }

  @Test
  void settersAndGetters_shouldWorkCorrectly() {
    User user = new User();
    user.setId(2L);
    user.setEmail("setter@example.com");
    user.setName("Setter User");
    user.setHouseholdId(20L);
    user.setPassword("anotherpassword");
    user.setRole(Role.ROLE_ADMIN);

    assertEquals(2L, user.getId());
    assertEquals("setter@example.com", user.getEmail());
    assertEquals("Setter User", user.getName());
    assertEquals(20L, user.getHouseholdId());
    assertEquals("anotherpassword", user.getPassword());
    assertEquals(Role.ROLE_ADMIN, user.getRole());
  }

  @Test
  void toString_shouldContainFieldValues() {
    User user = new User(
        1L,
        "test@example.com",
        "Test User",
        10L,
        "securepassword",
        Role.ROLE_NORMAL,
        false
    );

    String toStringResult = user.toString();
    assertTrue(toStringResult.contains("test@example.com"));
    assertTrue(toStringResult.contains("Test User"));
    assertTrue(toStringResult.contains("securepassword"));
    assertTrue(toStringResult.contains("ROLE_NORMAL"));
  }
}
