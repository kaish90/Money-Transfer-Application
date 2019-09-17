package com.application.storage;

import com.application.handlers.UserAccountHandler;
import com.application.handlers.impl.UserAccountHandlerImpl;
import com.application.models.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccountStorageImpl implements UserAccountStorage {
  private final Map<String, UserAccountHandler> storageMap = new ConcurrentHashMap<>();

  @Override
  public UserAccountHandler createUser(User user) {
    Optional<User> checkUser = Optional.ofNullable(user);
    if (checkUser.isPresent()) {
      return storageMap.computeIfAbsent(
          user.getUserId(),
          UserAccountHandler ->
              new UserAccountHandlerImpl(user.getUserId(), user.getUserAccountType()));
    }

    return null;
  }

  @Override
  public boolean checkIfUserExists(String userId) {
    return storageMap.get(userId) != null;
  }

  @Override
  public UserAccountHandler getUser(String userId) {
    if (checkIfUserExists(userId)) return storageMap.get(userId);
    return null;
  }
}
