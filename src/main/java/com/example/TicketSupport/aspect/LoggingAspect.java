package com.example.TicketSupport.aspect;

import com.example.TicketSupport.annotation.LogExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(
            ProceedingJoinPoint joinPoint,
            LogExecutionTime logExecutionTime) throws Throwable {

        long startTime = System.nanoTime();

        Object result = joinPoint.proceed();

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime) / 1_000_000;

        logger.info("Method '{}' executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTime);

        return result;
    }
}