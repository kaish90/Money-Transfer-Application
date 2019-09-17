package com.application;

import com.application.models.Transaction;
import com.application.models.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Web Client to test APIs functionality */
public class MoneyTransferApplicationClient {
  private Vertx vertx;
  private String uri;
  private WebClient client;
  private Logger log =
      io.vertx.core.logging.LoggerFactory.getLogger(MoneyTransferApplicationClient.class);
  private static final String CREATE_ACCOUNT_URL = "/user/create-account";

  /**
   * @param vertx
   * @param uri
   */
  public MoneyTransferApplicationClient(Vertx vertx, String uri) {
    this.vertx = vertx;
    this.uri = uri;
    client = WebClient.create(vertx);
  }

  /**
   * @param user
   * @return
   */
  public JsonObject createAccount(User user) {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    log.info("testing " + CREATE_ACCOUNT_URL);
    try {
      callApi(CREATE_ACCOUNT_URL, result, uri, client, HttpMethod.POST.name(), user);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result.get();
  }

  /**
   * @param transaction
   * @param userId
   * @return
   */
  public JsonObject withDraw(Transaction transaction, String userId) {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    final String api = String.format("/user/%s/withdraw", userId);
    log.info("testing " + api);
    try {
      callApi(api, result, uri, client, HttpMethod.POST.name(), transaction);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result.get();
  }

  /**
   * @param transaction
   * @param userId
   * @return
   */
  public JsonObject deposit(Transaction transaction, String userId) {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    final String api = String.format("/user/%s/deposit", userId);
    log.info("testing " + api);
    try {
      callApi(api, result, uri, client, HttpMethod.POST.name(), transaction);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result.get();
  }

  /**
   * @param transaction
   * @param userId
   * @return
   */
  public JsonObject transferMoney(Transaction transaction, String userId) {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    final String api = String.format("/user/%s/transfer-money", userId);
    log.info("testing " + api);
    try {
      callApi(api, result, uri, client, HttpMethod.POST.name(), transaction);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result.get();
  }

  /**
   * @param userId
   * @param accountID
   * @return
   */
  public JsonObject getAccountDetails(String userId, long accountID) {
    AtomicReference<JsonObject> result = new AtomicReference<>();
    final String api = String.format("/user/%s/account/%s", userId, accountID);
    log.info("testing " + api);
    try {
      callApi(api, result, uri, client, HttpMethod.GET.name(), null);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result.get();
  }

  /**
   * @param createAccountUrl
   * @param result
   * @param uri
   * @param client
   * @param httpMethod
   * @param requestJson
   * @throws InterruptedException
   */
  private void callApi(
      String createAccountUrl,
      AtomicReference<JsonObject> result,
      String uri,
      WebClient client,
      String httpMethod,
      Object requestJson)
      throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Runnable task =
        getRunnable(createAccountUrl, result, uri, client, requestJson, latch, httpMethod);
    new Thread(task).start();
    latch.await(10, TimeUnit.SECONDS);
  }

  /**
   * @param absUrl
   * @param result
   * @param uri
   * @param client
   * @param requestJson
   * @param latch
   * @param method
   * @return
   */
  private Runnable getRunnable(
      String absUrl,
      AtomicReference<JsonObject> result,
      String uri,
      WebClient client,
      Object requestJson,
      CountDownLatch latch,
      String method) {
    return () -> {
      HttpRequest<Buffer> request = null;
      try {
        final Handler<AsyncResult<HttpResponse<Buffer>>> asyncResultHandler =
            handler -> {
              if (handler.succeeded()) {
                log.info(handler.result().bodyAsString());
                result.set(handler.result().bodyAsJsonObject());

                latch.countDown();
              } else {

                log.info(handler.cause());
                latch.countDown();
              }
            };
        switch (method) {
          case "POST":
            request = client.postAbs(uri + absUrl);
            break;
          case "GET":
            request = client.getAbs(uri + absUrl);
            request.send(asyncResultHandler);
            break;
          case "PUT":
            request = client.putAbs(uri + absUrl);
            break;
        }
        request.sendJson(requestJson, asyncResultHandler);
      } catch (Exception e) {
        e.printStackTrace();
        latch.countDown();
      }
    };
  }
}
