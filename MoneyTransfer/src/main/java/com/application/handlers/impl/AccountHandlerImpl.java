package com.application.handlers.impl;

import com.application.handlers.AccountHandler;
import com.application.models.Account;
import com.application.models.Transaction;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class AccountHandlerImpl implements AccountHandler {

  private static final TreeMap<Long, Transaction> accountTransactions = new TreeMap<>();
  private AtomicLong balance;
  private final Long accountId;
  private Long lastHandledTransactionId = -1L;

  public AccountHandlerImpl(long accountId, long balance) {
    this.balance = new AtomicLong(balance);
    this.accountId = accountId;
  }

  /**
   * @param balance
   * @param accountType
   * @return Account
   */
  @Override
  public Account initAccount(long balance, String accountType) {
    Account account = new Account(accountId, balance, accountType);
    return account;
  }

  /** @return account balance */
  @Override
  public synchronized long calculateBalanceAmount() {
    NavigableSet<Long> keySet = accountTransactions.descendingKeySet();
    Iterator<Long> iterator = keySet.iterator();
    if (lastHandledTransactionId == -1L) {
      while (iterator.hasNext()) {
        handleTransaction(accountTransactions.get(iterator.next()));
      }
    } else {
      Transaction transaction;
      while (iterator.hasNext()
          && !((transaction = accountTransactions.get(iterator.next())).getTransactionId()
              == lastHandledTransactionId)) {
        handleTransaction(transaction);
      }
    }

    if (accountTransactions.size() != 0) {
      lastHandledTransactionId = keySet.iterator().next();
    }

    return balance.get();
  }

  /**
   * Handle Transactions to update the amount of account.
   *
   * @param transaction
   */
  private void handleTransaction(Transaction transaction) {
    if (transaction.getToAccount() == this.accountId) {
      balance.accumulateAndGet(transaction.getAmount(), (left, right) -> left + right);
    } else if (transaction.getFromAccount() == this.accountId) {
      balance.accumulateAndGet(transaction.getAmount() * -1, (left, right) -> left + right);
    }
  }

  @Override
  public Map<Long, Transaction> accountTransactions() {
    return Collections.synchronizedMap(accountTransactions);
  }
}
