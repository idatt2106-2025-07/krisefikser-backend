package com.group7.krisefikser.service.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * Service class for managing login attempts.
 * This class provides methods to track and manage login attempts for users.
 * It uses a cache to store the number of attempts and locks the user
 * after a certain number of failed attempts.
 */
@Service
public class LoginAttemptService {

  private static final int MAX_ATTEMPT = 4;
  private static final int LOCK_TIME_MINUTES = 10;
  private LoadingCache<String, Integer> attemptsCache;

  /**
   * Initializes the cache for tracking login attempts.
   * The cache expires after a specified time and initializes the attempt count to 0.
   */
  @PostConstruct
  public void init() {
    attemptsCache = CacheBuilder.newBuilder()
        .expireAfterWrite(LOCK_TIME_MINUTES, TimeUnit.MINUTES)
        .build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        });
  }

  /**
   * Resets the login attempts for a user.
   * This method is called when a user successfully logs in.
   *
   * @param username the username of the user
   */
  public void loginSucceeded(String username) {
    attemptsCache.invalidate(username);
  }

  /**
   * Increments the login attempts for a user.
   * This method is called when a user fails to log in.
   *
   * @param username the username of the user
   */
  public void loginFailed(String username) {
    int attempts = 0;
    try {
      attempts = attemptsCache.get(username);
    } catch (ExecutionException e) {
      return;
    }
    attempts++;
    attemptsCache.put(username, attempts);
  }

  /**
   * Checks if a user is blocked from logging in.
   * A user is blocked if they have exceeded the maximum number of login attempts.
   *
   * @param username the username of the user
   * @return true if the user is blocked, false otherwise
   */
  public boolean isBlocked(String username) {
    try {
      return attemptsCache.get(username) >= MAX_ATTEMPT;
    } catch (ExecutionException e) {
      return false;
    }
  }
}
