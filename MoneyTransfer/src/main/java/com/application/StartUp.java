package com.application;

import io.vertx.core.Vertx;

public class StartUp {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MoneyTransferApplication());
  }
}
