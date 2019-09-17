package com.application.handlers;

import com.application.models.Account;
import com.application.models.Transaction;

import java.util.Map;

public interface AccountHandler {

  Account initAccount(long balance, String accountType);

  long calculateBalanceAmount();

  Map<Long, Transaction> accountTransactions();
}
