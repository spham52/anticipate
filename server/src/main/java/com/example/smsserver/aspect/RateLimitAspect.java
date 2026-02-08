package com.example.smsserver.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
// aspect annotation class used to limit api requests
public class RateLimitAspect {

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        int requestsPerMinute = rateLimit.requestsPerMinute();
        return joinPoint.proceed();
    }
}
