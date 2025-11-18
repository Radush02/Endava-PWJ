package com.example.endavapwj.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginThrottle {

  private final RedisTemplate<String, String> redis;
  private final Duration failureWindow;
  private final Duration lockDuration;
  private final int maxAttempts;

  /**
   * Constructs the login throttling manager.
   *
   * @param redis Redis template used to store failure counters and lock markers
   * @param maxAttempts maximum failed attempts before the user is locked
   * @param failureWindow time window in which failed attempts are counted
   * @param lockDuration duration for which the account remains locked
   */
  public LoginThrottle(
      RedisTemplate<String, String> redis,
      @Value("${security.login.max-attempts}") int maxAttempts,
      @Value("${security.login.failure-window}") Duration failureWindow,
      @Value("${security.login.lock-duration}") Duration lockDuration) {
    this.redis = redis;
    this.maxAttempts = maxAttempts;
    this.failureWindow = failureWindow;
    this.lockDuration = lockDuration;
  }

  private String lockKey(long userId) {
    return "login:lock:" + userId;
  }

  private String failKey(long userId) {
    return "login:fail:" + userId;
  }

  /**
   * Returns the remaining lock time in seconds for a locked user.
   *
   * @param userId the user whose lock expiration is queried
   * @return remaining lock duration in seconds, or 0 if not locked
   */
  public long getLockRemainingSeconds(long userId) {
    Long ttl = redis.getExpire(lockKey(userId), TimeUnit.SECONDS);
    return (ttl == null || ttl < 0) ? 0 : ttl;
  }

  /**
   * Checks if a user account is currently locked.
   *
   * @param userId the user to check
   * @return true if the user is locked, false otherwise
   */
  public boolean isLocked(long userId) {
    return Boolean.TRUE.equals(redis.hasKey(lockKey(userId)));
  }

  /**
   * Resets login failure count and lock state for the given user. Call this method after a
   * successful login.
   *
   * @param userId the user for whom to clear throttling state
   */
  public void reset(long userId) {
    redis.delete(Arrays.asList(lockKey(userId), failKey(userId)));
  }

  /**
   * Registers a failed login attempt for the given user. If the maximum number of failures is
   * reached within the failure window, the account is locked for the configured lock duration.
   *
   * @param userId the user whose failure count is increased
   * @return the current number of recorded failed attempts for this window
   */
  public long registerFailure(long userId) {
    String fk = failKey(userId);
    if (isLocked(userId)) return maxAttempts;
    Long count = redis.opsForValue().increment(fk);
    if (count == null) count = 1L;
    if (count == 1L) {
      redis.expire(fk, failureWindow);
    }
    if (count >= maxAttempts) {
      redis.opsForValue().set(lockKey(userId), "1", lockDuration);
      redis.delete(fk);
    }
    return count;
  }
}
