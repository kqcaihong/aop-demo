package com.learn.more.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.learn.more.entiry.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.el.MethodNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller接口日志切面
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  // 用于入参、结果序列化
  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    MAPPER.registerModule(javaTimeModule);
  }

  // controller包下任意类中public方法，都是切点
  @Pointcut("execution(public * com.learn.more.controller.*.*(..))")
  public void log() {
  }

  @Around("log()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long elapsedTime = System.currentTimeMillis() - startTime;
    LogRecord logRecord = new LogRecord();
    logRecord.setStartTime(format(startTime));
    logRecord.setElapsedTime(elapsedTime);

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    assert attributes != null;
    HttpServletRequest request = attributes.getRequest();
    logRecord.setRemoteIp(request.getRemoteUser());
    logRecord.setUri(request.getRequestURI());
    logRecord.setMethod(request.getMethod());

    Method method = resolveMethod(joinPoint);
    Map<String, Object> parameterMap = getParameter(method, joinPoint.getArgs());
    logRecord.setParameter(parameterMap);
    logRecord.setResult(result);
    // 用打印 模拟保存数据库
    log.info(MAPPER.writeValueAsString(logRecord));

    return result;
  }

  // 解析切点对应的Method
  private Method resolveMethod(ProceedingJoinPoint point) {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Class<?> targetClass = point.getTarget().getClass();

    return getDeclaredMethod(targetClass, signature.getName(), signature.getMethod().getParameterTypes())
        .orElseThrow(() -> new MethodNotFoundException(signature.getMethod().getName()));
  }

  private Optional<Method> getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
    try {
      return Optional.of(clazz.getDeclaredMethod(name, parameterTypes));
    } catch (NoSuchMethodException e) {
      Class<?> superClass = clazz.getSuperclass();
      if (Objects.nonNull(superClass)) {
        return getDeclaredMethod(superClass, name, parameterTypes);
      }
    }
    return Optional.empty();
  }

  // 获取请求参数
  private Map<String, Object> getParameter(Method method, Object[] args) {
    Parameter[] parameters = method.getParameters();
    Map<String, Object> map = new HashMap<>();
    // 只记录springMVC框架注解标记参数
    for (int i = 0; i < parameters.length; i++) {
      RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
      RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
      PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
      String key = parameters[i].getName();
      if (Objects.nonNull(requestBody) || Objects.nonNull(requestParam) || Objects.nonNull(pathVariable)) {
        map.put(key, args[i]);
      }
    }
    return map;
  }

  // 格式化时间
  private String format(long milliseconds) {
    return format(new Date(milliseconds));
  }

  private String format(Date date) {
    Instant instant = date.toInstant();
    LocalDateTime time = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    return FORMATTER.format(time);
  }
}
