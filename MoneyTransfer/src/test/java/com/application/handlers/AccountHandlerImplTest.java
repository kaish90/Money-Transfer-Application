package com.application.handlers;

import com.application.handlers.impl.AccountHandlerImpl;
import com.application.models.Account;
import com.application.models.Transaction;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AccountHandlerImplTest {
  /** @param context */
  @Test
  public void initAccountTest(TestContext context) {
    long balance = 100l;
    long accountId = 1l;
    AccountHandler accountHandler = new AccountHandlerImpl(accountId, balance);
    Account account = accountHandler.initAccount(balance, "Savings");
    context.assertNotNull(account);
    context.assertEquals(account.getAccountID(), accountId);
    context.assertEquals(account.getBalance(), balance);
  }

  /** @param context */
  @Test
  public void calculateBalanceAmountTest(TestContext context) {
    long balance = 100l;
    long accountId = 1l;
    AccountHandler accountHandler = new AccountHandlerImpl(accountId, balance);
    Account account = accountHandler.initAccount(balance, "Savings");
    context.assertNotNull(account);
    final Transaction withdrawalTransaction = new Transaction(100l, accountId, null, 50l);
    final Transaction depositTransaction = new Transaction(100l, null, accountId, 50l);
    accountHandler.accountTransactions().put(1l, withdrawalTransaction);
    accountHandler.accountTransactions().put(2l, depositTransaction);
    context.assertEquals(accountHandler.calculateBalanceAmount(), account.getBalance());
    context.assertEquals(account.getBalance(), balance);
  }

  /** @param context */
  @Test
  public void accountTransactionsTest(TestContext context) {
    long balance = 100l;
    long accountId = 1l;
    AccountHandler accountHandler = new AccountHandlerImpl(accountId, balance);
    Account account = accountHandler.initAccount(balance, "Savings");
    context.assertNotNull(account);
    final Transaction withdrawalTransaction = new Transaction(100l, accountId, null, 50l);
    final Transaction depositTransaction = new Transaction(100l, null, accountId, 50l);
    accountHandler.accountTransactions().put(1l, withdrawalTransaction);
    accountHandler.accountTransactions().put(2l, depositTransaction);
    context.assertEquals(accountHandler.accountTransactions().size(), 2);
    context.assertEquals(accountHandler.accountTransactions().get(1l), withdrawalTransaction);
    context.assertEquals(accountHandler.accountTransactions().get(2l), depositTransaction);
    context.assertNotEquals(accountHandler.accountTransactions().get(1l), depositTransaction);
    context.assertNotEquals(accountHandler.accountTransactions().get(2l), withdrawalTransaction);
  }
}
