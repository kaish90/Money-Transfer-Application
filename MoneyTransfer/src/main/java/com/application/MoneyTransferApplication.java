package com.application;

import com.application.Constants.TransactionType;
import com.application.exception.UserAccountException;
import com.application.handlers.AccountHandler;
import com.application.handlers.TransactionHandler;
import com.application.handlers.UserAccountHandler;
import com.application.handlers.impl.AccountHandlerImpl;
import com.application.handlers.impl.TransactionHandlerImpl;
import com.application.models.Account;
import com.application.models.Transaction;
import com.application.models.User;
import com.application.storage.UserAccountStorage;
import com.application.storage.UserAccountStorageImpl;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.impl.HttpStatusException;

/** Application Class to define APIs route and Server */
public class MoneyTransferApplication extends AbstractVerticle {

  private final UserAccountStorage storage = new UserAccountStorageImpl();
  private HttpServer applicationServer;
  private static Logger logger = LoggerFactory.getLogger(MoneyTransferApplication.class);

  /**
   * Start Vert.x web server to host rest Apis
   *
   * @param startFuture Future Object
   */
  @Override
  public void start(Future<Void> startFuture) {
    HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
    applicationServer = vertx.createHttpServer(options);
    Router router = configureRoutes();
    applicationServer
        .requestHandler(router)
        .listen(
            8080,
            res -> {
              if (res.succeeded()) {
                logger.info("Server started successfully ..");
                startFuture.complete();
              } else {
                logger.info(
                    "Problems encountered during server startup, Please check stacktrace..");
                startFuture.fail(res.cause());
              }
            });
  }

  /** Configure routes to publish APIS */
  private Router configureRoutes() {
    Router router = Router.router(vertx);
    router
        .route(HttpMethod.POST, "/user/create-account")
        .consumes("application/json")
        .produces("application/json")
        .handler(BodyHandler.create())
        .handler(this::createUserAccount);
    router
        .route(HttpMethod.POST, "/user/:userId/withdraw")
        .produces("application/json")
        .consumes("application/json")
        .handler(BodyHandler.create())
        .handler(this::withdraw);
    router
        .route(HttpMethod.POST, "/user/:userId/deposit")
        .produces("application/json")
        .consumes("application/json")
        .handler(BodyHandler.create())
        .handler(this::deposit);
    router
        .route(HttpMethod.GET, "/user/:userId/account/:accountId")
        .produces("application/json")
        .handler(this::getUserAccountBalance);
    router
        .route(HttpMethod.POST, "/user/:userId/transfer-money")
        .consumes("application/json")
        .produces("application/json")
        .handler(BodyHandler.create())
        .handler(this::transferMoney);
    return router;
  }

  /**
   * Transfer money API implementation
   *
   * @param routingContext context to process request and return HTTPResponse
   */
  private void transferMoney(RoutingContext routingContext) {
    logger.info("transferMoney API Call received .");
    Transaction transaction = Json.decodeValue(routingContext.getBodyAsString(), Transaction.class);
    validateTransferMoneyRequest(routingContext, transaction);
    final String toAccountUser = transaction.getToAccountUser();
    if (toAccountUser != null && !toAccountUser.isEmpty()) {
      depositToDifferentUser(routingContext);
    } else deposit(routingContext);
    withdraw(routingContext);
    logger.info("transferMoney API Call completed .");
  }

  private void depositToDifferentUser(RoutingContext routingContext) {
    logger.info("depositToDifferentUser API Call received .");
    final HttpServerResponse response = routingContext.response();
    Account account = getAccountForTransactions(routingContext, response, TransactionType.TRANSFER);
    if (account == null) {
      logger.info("Something wrong with processing logic.");
      response
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end("Something wrong with processing logic. Please check logs.");
    }
    response.setStatusCode(200).end(Json.encodePrettily(account));
    logger.info("depositToDifferentUser API Call completed .");
  }

  /**
   * Validate Transfer Money Request received from User
   *
   * @param routingContext context to process request and return HTTPResponse
   * @param transaction Transaction Object
   */
  private void validateTransferMoneyRequest(
      RoutingContext routingContext, Transaction transaction) {
    Long fromAccount = transaction.getFromAccount();
    Long toAccount = transaction.getToAccount();
    long sum = transaction.getAmount();
    if (toAccount == null || fromAccount == null || sum <= 0) {
      routingContext
          .response()
          .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
          .end("Mandatory Fields missing in the request.");
    }
  }

  /**
   * Deposit API Logic
   *
   * @param routingContext context to process request and return HTTPResponse
   */
  private void deposit(RoutingContext routingContext) {
    logger.info("Deposit API Call received .");
    final HttpServerResponse response = routingContext.response();
    Account account = getAccountForTransactions(routingContext, response, TransactionType.DEPOSIT);
    if (account == null) {
      logger.info("Something wrong with processing logic.");
      response
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end("Something wrong with processing logic. Please check logs.");
    }
    response.setStatusCode(200).end(Json.encodePrettily(account));
    logger.info("Deposit API Call completed .");
  }

  /**
   * Common logic to handle transfer money say deposit and withdraw logic.
   *
   * @param routingContext context to process request and return HTTPResponse
   * @param response HttpServerResponse object to return response
   * @param transactionType Constants to identify type of transactions
   * @return Account Account object
   */
  private Account getAccountForTransactions(
      RoutingContext routingContext, HttpServerResponse response, TransactionType transactionType) {
    try {
      Transaction transaction =
          Json.decodeValue(routingContext.getBodyAsString(), Transaction.class);
      String userId;
      if (transactionType.equals(TransactionType.TRANSFER)) userId = transaction.getToAccountUser();
      else userId = routingContext.request().getParam("userId");

      if (!storage.checkIfUserExists(userId)) {
        response
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(String.format("User with id %s doesn't exist.", userId));
      }
      UserAccountHandler userAccountHandler = storage.getUser(userId);
      Account account;
      if (transactionType.equals(TransactionType.DEPOSIT)
          || transactionType.equals(TransactionType.TRANSFER)) {
        account = userAccountHandler.getUserAccountById(transaction.getToAccount());
      } else {
        account = userAccountHandler.getUserAccountById(transaction.getFromAccount());
        if (account.getBalance() < transaction.getAmount()) {
          response
              .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
              .end(
                  String.format(
                      "User with id %s doesn't have sufficient balance to perform this transaction.",
                      userId));
        }
      }
      performTransaction(transaction, account, transactionType);
      return account;
    } catch (HttpStatusException | DecodeException e) {
      UserAccountException accountException =
          new UserAccountException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      response
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(Json.encodePrettily(accountException));
    }
    return null;
  }

  /**
   * Withdraw API implementation
   *
   * @param routingContext context to process request and return HTTPResponse
   */
  private void withdraw(RoutingContext routingContext) {
    logger.info("withdraw API Call received .");
    final HttpServerResponse response = routingContext.response();
    Account account = getAccountForTransactions(routingContext, response, TransactionType.WITHDRAW);
    if (account == null) {
      logger.info("Something wrong with processing logic.");
      response
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end("Something wrong with processing logic. Please check logs.");
    }
    logger.info("withdraw API Call completed .");
    response.setStatusCode(HttpResponseStatus.OK.code()).end(Json.encodePrettily(account));
  }

  /**
   * Perform transactions safely and update the balance amount of relevant account
   *
   * @param transaction Transaction Object to perform transaction
   * @param account Account Object to get account details
   * @param transactionType Constants to identify type of transaction
   */
  private synchronized void performTransaction(
      Transaction transaction, Account account, TransactionType transactionType) {
    AccountHandler accountHandler =
        new AccountHandlerImpl(account.getAccountID(), account.getBalance());
    TransactionHandler transactionHandler;
    if (transactionType.equals(TransactionType.DEPOSIT)
        || transactionType.equals(TransactionType.TRANSFER))
      transactionHandler = new TransactionHandlerImpl(null, account, transaction.getAmount());
    else transactionHandler = new TransactionHandlerImpl(account, null, transaction.getAmount());
    accountHandler
        .accountTransactions()
        .put(account.getAccountID(), transactionHandler.transaction());
    account.setBalance(accountHandler.calculateBalanceAmount());
  }

  /**
   * Create User using user id
   *
   * @param routingContext context to process request and return HTTPResponse
   */
  private void createUserAccount(RoutingContext routingContext) {
    final HttpServerResponse response = routingContext.response();
    try {
      logger.info("createUserAccount API Call received .");
      User user = Json.decodeValue(routingContext.getBodyAsString(), User.class);
      logger.info("Received create-user request with details - " + user.toString());
      UserAccountHandler userAccountHandler = storage.createUser(user);
      if (userAccountHandler != null) {
        Account account = userAccountHandler.createUserAccount(user.getBalance());
        response.setStatusCode(HttpResponseStatus.CREATED.code()).end(Json.encodePrettily(account));
      }
      logger.info("createUserAccount API Call completed .");
      response
          .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
          .end(HttpResponseStatus.BAD_REQUEST.toString());
    } catch (HttpStatusException | DecodeException e) {
      UserAccountException accountException =
          new UserAccountException(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      response
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(Json.encodePrettily(accountException));
    }
  }

  /**
   * get account balance of requested user - Api implementation
   *
   * @param routingContext context to process request and return HTTPResponse
   */
  private void getUserAccountBalance(RoutingContext routingContext) {
    logger.info("getUserAccountBalance API Call received .");
    final HttpServerResponse response = routingContext.response();
    String userId = routingContext.request().getParam("userId");
    String accountId = routingContext.request().getParam("accountId");
    if (!storage.checkIfUserExists(userId)) {
      response
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(String.format("User with %s doesn't exist.", userId));
    }
    UserAccountHandler userAccountHandler = storage.getUser(userId);
    Account account = userAccountHandler.getUserAccountById(Long.valueOf(accountId));
    logger.info("getUserAccountBalance API Call completed .");
    response.setStatusCode(HttpResponseStatus.OK.code()).end(Json.encodePrettily(account));
  }

  /**
   * call when vert.x web server stops
   *
   * @param stopFuture Future Object
   */
  @Override
  public void stop(Future<Void> stopFuture) {
    applicationServer.close(
        stop -> {
          if (stop.succeeded()) {
            stopFuture.complete();
          } else {
            stopFuture.fail(stop.cause());
          }
        });
  }
}
