package com.application.exception;

import java.io.Serializable;

public class UserAccountException implements Serializable {
  private String message;
  private int httpStatus;

  public UserAccountException(String message, int httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }
}
