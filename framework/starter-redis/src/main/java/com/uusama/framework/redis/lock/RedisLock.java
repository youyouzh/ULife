package com.uusama.framework.redis.lock;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * @author zhaohai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLock {
    private final RedissonClient redissonClient;

    public RLock getLock(@NonNull String lockedKey) {
        return redissonClient.getLock(lockedKey);
    }

    public void runWithLock(@NonNull String lockedKey, Runnable runnable) {
        runWithLock(runnable, getLock(lockedKey));
    }

    public <T> T runWithLock(@NonNull String lockedKey, Supplier<T> runnable) {
        return runWithLock(runnable, getLock(lockedKey));
    }

    private void runWithLock(Runnable runnable, Lock lock) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private <T> T runWithLock(Supplier<T> runnable, Lock lock) {
        lock.lock();
        try {
            return runnable.get();
        } finally {
            lock.unlock();
        }
    }

    public boolean tryRunWithLock(@NonNull String lockedKey, Runnable runnable) {
        return tryRunWithLock(runnable, getLock(lockedKey));
    }

    public void checkAndRunWithLock(@NonNull String lockedKey, Runnable runnable) {
        boolean run = tryRunWithLock(runnable, getLock(lockedKey));
        if (!run) {
            throw new RuntimeException("任务执行中，请勿重复请求 key:" + lockedKey);
        }
    }

    private boolean tryRunWithLock(Runnable runnable, Lock lock) {
        if (!lock.tryLock()) {
            return false;
        }
        try {
            runnable.run();
            return true;
        } finally {
            lock.unlock();
        }
    }
}
