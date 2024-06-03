package com.learn.more.controller;

import com.learn.more.entiry.User;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController implements InitializingBean {

  // 生成ID
  private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
  // 模拟数据库保存记录
  private static final Map<Long, User> USER_MAP = new ConcurrentHashMap<>();

  @GetMapping("/queryAll")
  public List<User> queryAll() {
    return USER_MAP.values().stream().sorted(Comparator.comparingLong(User::getId)).collect(Collectors.toList());
  }

  @PostMapping("/add")
  public User addByParam(@RequestParam String name, @RequestParam int age) {
    User user = new User(name, age);
    user.setId(ID_GENERATOR.incrementAndGet());
    USER_MAP.put(user.getId(), user);
    return user;
  }

  // 初始化一条记录
  @Override
  public void afterPropertiesSet() {
    User bob = new User(ID_GENERATOR.incrementAndGet(), "Bob", 33);
    USER_MAP.put(bob.getId(), bob);
  }
}
