package com.example;

import java.util.concurrent.atomic.AtomicLong;

public class UserIdGenerator {
    private static final AtomicLong lastUserId = new AtomicLong(System.currentTimeMillis());

    public static long generateUserId() {
        return lastUserId.incrementAndGet();
    }
}