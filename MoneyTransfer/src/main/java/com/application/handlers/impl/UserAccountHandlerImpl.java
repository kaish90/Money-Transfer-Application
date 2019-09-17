package com.application.handlers.impl;

import com.application.handlers.UserAccountHandler;
import com.application.models.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserAccountHandlerImpl implements UserAccountHandler {
  private String userId;
  private static AtomicLong ACCOUNT_ID;
  private static Map<Long, Account> accountById;
  private String accountType;

  static {
    ACCOUNT_ID = new AtomicLong(1);
    accountById = new ConcurrentHashMap<>();
  }

  public UserAccountHandlerImpl(String userId, String accountType) {
    this.userId = userId;
    this.accountType = accountType;
  }

  @Override
  public Account createUserAccount(long amount) {
    AccountHandlerImpl accountHandler =
        new AccountHandlerImpl(ACCOUNT_ID.getAndIncrement(), amount);
    Account account = accountHandler.initAccount(amount, accountType);
    accountById.put(account.getAccountID(), account);
    return account;
  }

  @Override
  public Account getUserAccountById(long accountID) {
    return accountById.get(accountID);
  }

  @Override
  public String getUserId() {
    return userId;
  }
}
