package com.example.endavapwj.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class LoginThrottle {

    private final RedisTemplate<String, String> redis;
    private final Duration failureWindow;
    private final Duration lockDuration;
    private final int maxAttempts;

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

    public long getLockRemainingSeconds(long userId) {
        Long ttl = redis.getExpire(lockKey(userId), TimeUnit.SECONDS);
        return (ttl == null || ttl < 0) ? 0 : ttl;
    }

    public boolean isLocked(long userId) {
        return Boolean.TRUE.equals(redis.hasKey(lockKey(userId)));
    }

    public void reset(long userId) {
        redis.delete(Arrays.asList(lockKey(userId), failKey(userId)));
    }

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