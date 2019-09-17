package com.application.handlers.impl;

import com.application.handlers.TransactionHandler;
import com.application.models.Account;
import com.application.models.Transaction;

import java.util.concurrent.atomic.AtomicLong;

public class TransactionHandlerImpl implements TransactionHandler {
  private static AtomicLong TRANSACTION_ID_GENERATE = new AtomicLong(100);
  private Transaction transaction;
  private long transactionId;
  private Account fromAccount;
  private Account toAccount;
  private long amount;

  public TransactionHandlerImpl(Account fromAccount, Account toAccount, long amount) {
    transactionId = TRANSACTION_ID_GENERATE.getAndIncrement();
    this.fromAccount = fromAccount;
    this.toAccount = toAccount;
    this.amount = amount;
    transaction = getTransaction(fromAccount, toAccount, amount);
  }

  private Transaction getTransaction(Account fromAccount, Account toAccount, long amount) {
    return new Transaction(
        transactionId,
        (null != fromAccount) ? fromAccount.getAccountID() : null,
        (null != toAccount) ? toAccount.getAccountID() : null,
        amount);
  }

  @Override
  public long transactionId() {
    return transactionId;
  }

  @Override
  public Account fromAccount() {
    return fromAccount;
  }

  @Override
  public Account toAccount() {
    return toAccount;
  }

  @Override
  public long transactionMoney() {
    return amount;
  }

  @Override
  public Transaction transaction() {
    return transaction;
  }
}
