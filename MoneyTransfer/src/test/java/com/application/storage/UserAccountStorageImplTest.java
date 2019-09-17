package com.application.storage;

import com.application.handlers.UserAccountHandler;
import com.application.handlers.impl.UserAccountHandlerImpl;
import com.application.models.User;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/** UT for UserAccountStorageImpl */
@RunWith(VertxUnitRunner.class)
public class UserAccountStorageImplTest {

  /** @param context */
  @Test
  public void createUserTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    User userToTest2 = createUserToTest("user2", "user2", "current", 100l);
    UserAccountHandler handler =
        new UserAccountHandlerImpl(userToTest1.getUserId(), userToTest1.getUserAccountType());
    UserAccountHandler handler2 =
        new UserAccountHandlerImpl(userToTest2.getUserId(), userToTest2.getUserAccountType());
    UserAccountStorageImpl userAccountStorage = new UserAccountStorageImpl();
    userAccountStorage.createUser(userToTest1);
    userAccountStorage.createUser(userToTest2);
    final UserAccountHandler user1 = userAccountStorage.getUser(userToTest1.getUserId());
    final UserAccountHandler user2 = userAccountStorage.getUser(userToTest2.getUserId());
    context.assertNotNull(user1);
    context.assertEquals(user1.getUserId(), handler.getUserId());
    context.assertNotNull(user2);
    context.assertEquals(user2.getUserId(), handler2.getUserId());
  }

  /** @param context */
  @Test
  public void checkIfUserExistsTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    UserAccountStorageImpl userAccountStorage = new UserAccountStorageImpl();
    userAccountStorage.createUser(userToTest1);
    context.assertNotNull(userAccountStorage);
    context.assertEquals(userAccountStorage.checkIfUserExists(userToTest1.getUserId()), true);
    context.assertEquals(userAccountStorage.checkIfUserExists("Unknown"), false);
  }

  /** @param context */
  @Test
  public void getUserTest(TestContext context) {
    User userToTest1 = createUserToTest("user1", "user1", "saving", 0l);
    UserAccountStorageImpl userAccountStorage = new UserAccountStorageImpl();
    userAccountStorage.createUser(userToTest1);
    UserAccountHandler handler =
        new UserAccountHandlerImpl(userToTest1.getUserId(), userToTest1.getUserAccountType());
    context.assertNotNull(userAccountStorage);
    context.assertEquals(
        userAccountStorage.getUser(userToTest1.getUserId()).getUserId(), handler.getUserId());
    context.assertEquals(userAccountStorage.getUser("Unknown"), null);
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
