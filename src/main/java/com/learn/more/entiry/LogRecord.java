package com.learn.more.entiry;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogRecord {
  /**
   * 操作时间
   */
  private String startTime;

  /**
   * 消时，单位：毫秒
   */
  private Long elapsedTime;

  /**
   * uri
   */
  private String uri;

  /**
   * 请求类型
   */
  private String method;

  /**
   * IP地址
   */
  private String remoteIp;

  /**
   * 请求参数
   */
  private Object parameter;

  /**
   * 请求返回的结果
   */
  private Object result;
}
