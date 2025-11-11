package com.library.librarymanagement.service.account;

import io.github.bucket4j.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    public boolean tryConsume(String key) {
        Bucket bucket = cache.computeIfAbsent(key, this::newBucket);
        return bucket.tryConsume(1);
    }

    private Bucket newBucket(String key) {
        Refill refill = Refill.greedy(1, Duration.ofMinutes(5));
        Bandwidth limit = Bandwidth.classic(1, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
