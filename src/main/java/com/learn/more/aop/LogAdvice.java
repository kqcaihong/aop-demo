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
public class LogAdvice {

  @Pointcut("execution(* com.learn.more.controller.*.*(..))")
  public void logPoincut() {
  }

  @Before("logPoincut()")
  public void beforeAdvice() {
    log.info("beforeAdvice...");
  }

  @After("logPoincut()")
  public void afterAdvice() {
    log.info("afterAdvice...");
  }

  @Around("logPoincut()")
  public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    log.info("Around before");
    try {
      Object result = proceedingJoinPoint.proceed();
      log.info("result: {}", result);
      return result;
    } catch (Throwable t) {
      log.error("error: ", t);
      throw t;
    } finally {
      log.info("Around after");
    }
  }
}
