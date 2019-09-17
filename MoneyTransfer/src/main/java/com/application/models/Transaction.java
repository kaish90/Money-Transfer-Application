package com.application.models;

public class Transaction {

  private Long fromAccount;
  private Long toAccount;
  private long amount;
  private long timeStamp;
  private long transactionId;
  private String toAccountUser;

  public Transaction() {
    super();
  }

  public long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(long transactionId) {
    this.transactionId = transactionId;
  }

  public Transaction(long transactionId, Long fromAccount, Long toAccount, long amount) {
    this.transactionId = transactionId;
    this.fromAccount = fromAccount;
    this.toAccount = toAccount;
    this.amount = amount;
    timeStamp = System.currentTimeMillis();
  }

  public Long getFromAccount() {
    return fromAccount;
  }

  public void setFromAccount(Long fromAccount) {
    this.fromAccount = fromAccount;
  }

  public Long getToAccount() {
    return toAccount;
  }

  public void setToAccount(Long toAccount) {
    this.toAccount = toAccount;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getToAccountUser() {
    return toAccountUser;
  }

  public void setToAccountUser(String toAccountUser) {
    this.toAccountUser = toAccountUser;
  }

  @Override
  public String toString() {
    return "Transaction{"
        + "fromAccount="
        + fromAccount
        + ", toAccount="
        + toAccount
        + ", amount="
        + amount
        + ", timeStamp="
        + timeStamp
        + ", transactionId="
        + transactionId
        + ", toAccountUser='"
        + toAccountUser
        + '\''
        + '}';
  }
}
