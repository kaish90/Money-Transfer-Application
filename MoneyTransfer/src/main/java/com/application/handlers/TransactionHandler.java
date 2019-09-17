package com.application.handlers;

import com.application.models.Account;
import com.application.models.Transaction;

public interface TransactionHandler {

  long transactionId();

  Account fromAccount();

  Account toAccount();

  long transactionMoney();

  Transaction transaction();
}
