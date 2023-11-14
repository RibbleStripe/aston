package com.example.aston.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    @Before("execution(* com.example.aston.service.*.*(..))")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        System.out.println("Executing method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.example.aston.service.*.*(..))", returning = "result")
    public void afterMethodExecution(JoinPoint joinPoint, Object result) {
        System.out.println("Method " + joinPoint.getSignature().getName() + " executed. Result: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.example.aston.service.*.*(..))", throwing = "exception")
    public void afterMethodThrowing(JoinPoint joinPoint, Throwable exception) {
        System.out.println("Error in method " + joinPoint.getSignature().getName() + ": " + exception.getMessage());
    }
}
