package io.github.gabrielmmoraes1999.service.service;

import io.github.gabrielmmoraes1999.service.entity.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final static Map<Integer, User> fakeDB = new HashMap<>();

    public User findById(int id) {
        return fakeDB.getOrDefault(id, null);
    }

    public User save(User user) {
        user.setId(user.getId());
        fakeDB.put(user.getId(), user);
        return user;
    }

    public void delete(int id) {
        fakeDB.remove(id);
    }

    public Collection<User> getUsers() {
        return fakeDB.values();
    }
}