package com.learn.more.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AopTest {

  @Pointcut("execution(* com.learn.more.controller.*.*(..))")
  public void pointcut() {
  }

  @Before("pointcut()")
  public void beforeAdvice() {
    log.info("before advice...");
  }

  @After("pointcut()")
  public void afterAdvice() {
    log.info("after advice...");
  }

  @Around("pointcut()")
  public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    log.info("around before");
    try {
      Object result = proceedingJoinPoint.proceed();
      log.info("around result: {}", result);
      return result;
    } catch (Throwable t) {
      log.error("around error: ", t);
      throw t;
    } finally {
      log.info("around after");
    }
  }
}
