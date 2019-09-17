package com.application.models;

import java.io.Serializable;

public class User implements Serializable {
  private String userName;
  private String userId;
  private String userAccountType;
  private long balance;

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }

  public User() {
    super();
  }

  public User(String userName, String userId, String userAccountType, long balance) {
    this.userName = userName;
    this.userId = userId;
    this.userAccountType = userAccountType;
    this.balance = balance;
  }

  public User(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserAccountType() {
    return userAccountType;
  }

  public void setUserAccountType(String userAccountType) {
    this.userAccountType = userAccountType;
  }

  @Override
  public String toString() {
    return "User{"
        + "userName='"
        + userName
        + '\''
        + ", userId='"
        + userId
        + '\''
        + ", userAccountType='"
        + userAccountType
        + '\''
        + ", balance="
        + balance
        + '}';
  }
}
