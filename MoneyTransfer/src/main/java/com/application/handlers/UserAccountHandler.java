package com.application.handlers;

import com.application.models.Account;

public interface UserAccountHandler {
  Account createUserAccount(long amount);

  Account getUserAccountById(long accountID);

  String getUserId();
}
