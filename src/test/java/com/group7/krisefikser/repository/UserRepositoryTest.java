package com.group7.krisefikser.repository;

import com.group7.krisefikser.enums.Role;
import com.group7.krisefikser.model.user.User;
import com.group7.krisefikser.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_existingUser_returnsUser() {
    Optional<User> result = userRepository.findByEmail("user@example.com");

    assertTrue(result.isPresent());
    User user = result.get();
    assertEquals("user@example.com", user.getEmail());
    assertEquals("Bob User", user.getName());
    assertEquals(1L, user.getHouseholdId());
    assertEquals(Role.ROLE_NORMAL, user.getRole());
  }

  @Test
  void findByEmail_nonExistingUser_returnsEmpty() {
    Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
    assertTrue(result.isEmpty());
  }

  @Test
  void findById_existingUser_returnsUser() {
    Optional<User> result = userRepository.findById(1L);

    assertTrue(result.isPresent());
    User user = result.get();
    assertEquals("admin@example.com", user.getEmail());
    assertEquals("Alice Admin", user.getName());
    assertEquals(1L, user.getHouseholdId());
    assertEquals(Role.ROLE_ADMIN, user.getRole());
  }

  @Test
  void findById_nonExistingUser_returnsEmpty() {
    Optional<User> result = userRepository.findById(999L);
    assertTrue(result.isEmpty());
  }

  @Test
  void save_validUser_savesAndReturnsUser() {
    User user = new User();
    user.setEmail("newuser@example.com");
    user.setName("New User");
    user.setPassword("securepassword");
    user.setHouseholdId(1L);

    Optional<User> savedUser = userRepository.save(user);

    assertTrue(savedUser.isPresent());
    assertEquals("newuser@example.com", savedUser.get().getEmail());
    assertEquals(Role.ROLE_NORMAL, savedUser.get().getRole());
  }

  @Test
  void save_userWithNullEmail_failsAndReturnsEmpty() {
    User user = new User();
    user.setName("Broken User");
    user.setPassword("password");
    user.setHouseholdId(1L);

    Optional<User> result = userRepository.save(user);
    assertTrue(result.isEmpty());
  }

  @Test
  void deleteById_deletesUserAndRelatedRequests() {
    // Først lag og lagre en ny bruker
    User user = new User();
    user.setEmail("tobedeleted@example.com");
    user.setName("To Be Deleted");
    user.setPassword("pw");
    user.setHouseholdId(1L);
    Optional<User> savedUser = userRepository.save(user);
    assertTrue(savedUser.isPresent());

    Long userId = savedUser.get().getId();
    userRepository.deleteById(userId);

    Optional<User> deleted = userRepository.findById(userId);
    assertTrue(deleted.isEmpty());
  }

  @Test
  void findByRole_returnsCorrectUsers() {
    List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
    assertFalse(admins.isEmpty());
    assertTrue(admins.stream().allMatch(user -> user.getRole() == Role.ROLE_ADMIN));
  }

  @Test
  void updatePasswordByEmail_changesPasswordSuccessfully() {
    Optional<User> userOpt = userRepository.findByEmail("user@example.com");
    assertTrue(userOpt.isPresent());

    userRepository.updatePasswordByEmail("user@example.com", "newhashedpassword");

    Optional<User> updated = userRepository.findByEmail("user@example.com");
    assertTrue(updated.isPresent());
    assertEquals("newhashedpassword", updated.get().getPassword());
  }
}