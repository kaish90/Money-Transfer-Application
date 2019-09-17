package com.application;

import com.application.models.Account;
import com.application.models.Transaction;
import com.application.models.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(VertxUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoneyTransferApplicationTest {
  private String uri = "http://localhost:8080";
  private Vertx vertx = Vertx.vertx();
  private MoneyTransferApplicationClient client = new MoneyTransferApplicationClient(vertx, uri);
  private String deploymentId;
  private io.vertx.core.logging.Logger log =
      LoggerFactory.getLogger(MoneyTransferApplicationTest.class);

  /**
   * Test create-account API
   *
   * @param context
   * @throws Exception
   */
  @Test
  public void createAccountTest(TestContext context) throws Exception {
    User user = new User();
    user.setUserAccountType("Savings");
    user.setUserId("User1");
    user.setUserName("User");
    user.setBalance(500);
    JsonObject result = client.createAccount(user);
    log.info("Result = " + result);
    Account account = Json.decodeValue(result.toBuffer(), Account.class);
    context.assertNotNull(account.getAccountID());
    context.assertEquals(account.getBalance(), user.getBalance());
    context.assertEquals(account.getAccountType(), user.getUserAccountType());
  }

  /**
   * Test withdraw API
   *
   * @param context
   * @throws Exception
   */
  @Test
  public void withDrawFromAccountTest(TestContext context) throws Exception {
    JsonObject createdAccountJson = createUserToTest("kaish", "kaishal", "Current", 400);
    Account createdAccount = Json.decodeValue(createdAccountJson.toBuffer(), Account.class);
    Transaction transaction = new Transaction();
    transaction.setFromAccount(createdAccount.getAccountID());
    transaction.setAmount(100);
    JsonObject result = client.withDraw(transaction, "kaish");
    log.info("Result = " + result);
    Account account = Json.decodeValue(result.toBuffer(), Account.class);
    context.assertEquals(account.getAccountID(), createdAccount.getAccountID());
    context.assertEquals(account.getBalance(), createdAccount.getBalance() - 100);
  }

  /**
   * Test Deposit API
   *
   * @param context
   * @throws Exception
   */
  @Test
  public void depositToAccountTest(TestContext context) throws Exception {
    JsonObject createdAccountJson = createUserToTest("kaish", "kaishal", "Current", 400);
    Account createdAccount = Json.decodeValue(createdAccountJson.toBuffer(), Account.class);
    Transaction transaction = new Transaction();
    transaction.setToAccount(createdAccount.getAccountID());
    transaction.setAmount(100);
    JsonObject result = client.deposit(transaction, "kaish");
    log.info("Result = " + result);
    Account account = Json.decodeValue(result.toBuffer(), Account.class);
    context.assertEquals(account.getAccountID(), createdAccount.getAccountID());
    context.assertEquals(account.getBalance(), createdAccount.getBalance() + 100);
  }

  /**
   * Test transfer-money API
   *
   * @param context
   * @throws Exception
   */
  @Test
  public void transferMoneyAccountTest(TestContext context) throws Exception {
    JsonObject createdAccountJson = createUserToTest("kaish", "kaishal", "Current", 400);
    Account createdAccount = Json.decodeValue(createdAccountJson.toBuffer(), Account.class);
    context.assertNotNull(createdAccount);
    Transaction transaction = new Transaction();
    transaction.setToAccount(createdAccount.getAccountID());
    transaction.setAmount(100);
    transaction.setFromAccount(1l);
    JsonObject result = client.transferMoney(transaction, "kaish");
    log.info("Result = " + result);
    Account account = Json.decodeValue(result.toBuffer(), Account.class);
    context.assertEquals(account.getAccountID(), createdAccount.getAccountID());
    context.assertEquals(account.getBalance(), createdAccount.getBalance() + 100);
  }

  /**
   * Test getAccountDetails API
   *
   * @param context
   * @throws Exception
   */
  @Test
  public void getAccountDetailsTest(TestContext context) throws Exception {
    JsonObject accountObject = createUserToTest("kaishal", "kaishal", "Current", 200);
    Account createdAccount = Json.decodeValue(accountObject.toBuffer(), Account.class);
    context.assertNotNull(createdAccount);
    JsonObject result = client.getAccountDetails("kaishal", createdAccount.getAccountID());
    log.info("Result = " + result);
    Account account = Json.decodeValue(result.toBuffer(), Account.class);
    context.assertEquals(account.getAccountID(), createdAccount.getAccountID());
    context.assertEquals(account.getBalance(), createdAccount.getBalance());
  }
  /**
   * @param userId
   * @param name
   * @param type
   * @param balance
   */
  private JsonObject createUserToTest(String userId, String name, String type, long balance) {
    User user = new User();
    user.setUserAccountType(type);
    user.setUserId(userId);
    user.setUserName(name);
    user.setBalance(balance);
    return client.createAccount(user);
  }

  @Before
  public void before(TestContext context) {
    Async async = context.async();
    vertx.deployVerticle(
        new MoneyTransferApplication(),
        event -> {
          async.countDown();
          deploymentId = event.result();
        });
    async.await();
  }

  @After
  public void after(TestContext context) {
    Async async = context.async();
    vertx.undeploy(deploymentId, event -> async.countDown());
    async.await();
  }
}
