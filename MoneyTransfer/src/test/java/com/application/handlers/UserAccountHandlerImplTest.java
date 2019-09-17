package com.application.handlers;

import com.application.handlers.impl.UserAccountHandlerImpl;
import com.application.models.Account;
import com.application.models.User;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/** UT for UserAccountHandlerImpl */
@RunWith(VertxUnitRunner.class)
public class UserAccountHandlerImplTest {

  /** @param context */
  @Test
  public void createUserAccountTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    User userToTest2 = createUserToTest("user2", "user2", "current", 100l);
    UserAccountHandler handler =
        new UserAccountHandlerImpl(userToTest1.getUserId(), userToTest1.getUserAccountType());
    UserAccountHandler handler2 =
        new UserAccountHandlerImpl(userToTest2.getUserId(), userToTest2.getUserAccountType());
    Account account = handler.createUserAccount(userToTest1.getBalance());
    Account account2 = handler2.createUserAccount(userToTest2.getBalance());
    context.assertNotNull(handler);
    context.assertEquals(account.getAccountType(), userToTest1.getUserAccountType());
    context.assertEquals(account.getBalance(), userToTest1.getBalance());
    context.assertNotNull(handler2);
    context.assertEquals(account2.getAccountType(), userToTest2.getUserAccountType());
    context.assertEquals(account2.getBalance(), userToTest2.getBalance());
  }

  /** @param context */
  @Test
  public void getUserAccountByIdTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    User userToTest2 = createUserToTest("user2", "user2", "current", 100l);
    UserAccountHandler handler =
        new UserAccountHandlerImpl(userToTest1.getUserId(), userToTest1.getUserAccountType());
    UserAccountHandler handler2 =
        new UserAccountHandlerImpl(userToTest2.getUserId(), userToTest2.getUserAccountType());
    Account account = handler.createUserAccount(userToTest1.getBalance());
    Account account2 = handler2.createUserAccount(userToTest2.getBalance());

    context.assertNotNull(handler);
    context.assertEquals(
        handler.getUserAccountById(account.getAccountID()).getAccountID(), account.getAccountID());
    context.assertEquals(
        handler.getUserAccountById(account.getAccountID()).getBalance(), account.getBalance());
    context.assertNotNull(handler2);
    context.assertEquals(
        handler2.getUserAccountById(account2.getAccountID()).getAccountID(),
        account2.getAccountID());
    context.assertEquals(
        handler2.getUserAccountById(account2.getAccountID()).getBalance(), account2.getBalance());
  }

  /** @param context */
  @Test
  public void getUserIdTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    User userToTest2 = createUserToTest("user2", "user2", "current", 100l);
    UserAccountHandler handler =
        new UserAccountHandlerImpl(userToTest1.getUserId(), userToTest1.getUserAccountType());
    UserAccountHandler handler2 =
        new UserAccountHandlerImpl(userToTest2.getUserId(), userToTest2.getUserAccountType());
    String userId1 = handler.getUserId();
    String userId2 = handler2.getUserId();
    context.assertNotNull(handler);
    context.assertNotNull(handler2);
    context.assertEquals(userId1, userToTest1.getUserId());
    context.assertEquals(userId2, userToTest2.getUserId());
    context.assertNotEquals(userId1, userToTest2.getUserId());
    context.assertNotEquals(userId2, userToTest1.getUserId());
  }

  /**
   * @param userId
   * @param name
   * @param type
   * @param balance
   * @return
   */
  private User createUserToTest(String userId, String name, String type, long balance) {
    User user = new User();
    user.setUserAccountType(type);
    user.setUserId(userId);
    user.setUserName(name);
    user.setBalance(balance);
    return user;
  }
}
