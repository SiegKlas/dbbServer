package ru.nsu.michael.dbbserver;

import java.util.HashSet;

public class Session {
    public String id;
    public final HashSet<User> users = new HashSet<>();

    Session(String id, User user) {
        this.id = id;
        this.users.add(user);
    }
}
