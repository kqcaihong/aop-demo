package com.learn.more.entiry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private Long id;
  private String name;
  private int age;

  public User(String name, int age) {
    this.name = name;
    this.age = age;
  }
}
