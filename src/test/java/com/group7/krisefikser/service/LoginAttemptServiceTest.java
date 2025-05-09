package com.group7.krisefikser.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.group7.krisefikser.service.user.LoginAttemptService;
import java.lang.reflect.Field;



import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

  private LoginAttemptService loginAttemptService;

  @BeforeEach
  void setUp() {
    loginAttemptService = new LoginAttemptService();
    loginAttemptService.init();
  }

  @Test
  void loginFailed_shouldIncrementAttempts() throws Exception {
    String username = "user1";

    // 1st failed attempt
    loginAttemptService.loginFailed(username);
    int attemptsAfterFirst = getAttemptCount(username);
    assertThat(attemptsAfterFirst).isEqualTo(1);

    // 2nd failed attempt
    loginAttemptService.loginFailed(username);
    int attemptsAfterSecond = getAttemptCount(username);
    assertThat(attemptsAfterSecond).isEqualTo(2);
  }

  @Test
  void loginSucceeded_shouldResetAttempts() throws Exception {
    String username = "user2";

    loginAttemptService.loginFailed(username);
    loginAttemptService.loginFailed(username);
    assertThat(getAttemptCount(username)).isEqualTo(2);

    loginAttemptService.loginSucceeded(username);
    // After successful login, the entry should be invalidated
    assertThat(getAttemptCount(username)).isEqualTo(0);
  }

  @Test
  void isBlocked_shouldReturnFalseWhenUnderLimit() {
    String username = "user3";

    loginAttemptService.loginFailed(username);
    loginAttemptService.loginFailed(username);

    boolean blocked = loginAttemptService.isBlocked(username);
    assertThat(blocked).isFalse();
  }

  @Test
  void isBlocked_shouldReturnTrueAfterMaxAttempts() {
    String username = "user4";

    loginAttemptService.loginFailed(username);
    loginAttemptService.loginFailed(username);
    loginAttemptService.loginFailed(username);
    loginAttemptService.loginFailed(username);

    boolean blocked = loginAttemptService.isBlocked(username);
    assertThat(blocked).isTrue();
  }

  // Helper to access internal attempts count (for testing only)
  private int getAttemptCount(String username) throws Exception {
    Field cacheField = LoginAttemptService.class.getDeclaredField("attemptsCache");
    cacheField.setAccessible(true);
    LoadingCache<String, Integer> cache = (LoadingCache<String, Integer>) cacheField.get(loginAttemptService);
    return cache.get(username);
  }
}
