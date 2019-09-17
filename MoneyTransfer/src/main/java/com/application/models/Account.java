package com.application.models;

public class Account {

  private long accountID;
  private long balance;
  private String accountType;

  public Account(long accountId, long balance, String accountType) {
    this.accountID = accountId;
    this.balance = balance;
    this.accountType = accountType;
  }

  public Account() {
    super();
  }

  public long getAccountID() {
    return accountID;
  }

  public void setAccountID(Long accountID) {
    this.accountID = accountID;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }

  public String getAccountType() {
    return accountType;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  @Override
  public String toString() {
    return "Account{"
        + "accountID="
        + accountID
        + ", balance="
        + balance
        + ", accountType='"
        + accountType
        + '\''
        + '}';
  }
}
