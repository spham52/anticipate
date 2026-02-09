package com.example.smsserver.aspect;

import com.example.smsserver.exception.RateLimitExceededException;
import com.example.smsserver.utils.HttpReqRespUtils;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
// aspect annotation class used to limit api requests
public class RateLimitAspect {

    // maps user's IP address and endpoint to their respective token bucket
    ConcurrentHashMap<String, Bucket> bucketMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        int requestsPerMinute = rateLimit.requestsPerMinute();

        // get user ip address
        String ip = HttpReqRespUtils.getClientIpAddress();

        // get method name (endpoint)
        String methodName = joinPoint.getSignature().getName();

        // concentate ip and methodname string
        String key = ip + methodName;

        // if user does not have a bucket associated with that endpoint, create one and store in hashmap
        Bucket bucket = bucketMap.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(limit ->
                                limit.capacity(requestsPerMinute)
                                        .refillGreedy(requestsPerMinute, Duration.ofMinutes(1)))
                        .build()
        );

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Too many requests", ip);
        }
    }
}
