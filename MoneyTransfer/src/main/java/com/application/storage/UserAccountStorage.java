package com.application.storage;

import com.application.handlers.UserAccountHandler;
import com.application.models.User;

public interface UserAccountStorage {

  UserAccountHandler createUser(User user);

  boolean checkIfUserExists(String userId);

  UserAccountHandler getUser(String userId);
}
