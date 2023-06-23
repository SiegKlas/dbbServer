package ru.nsu.michael.dbbserver;

import java.util.HashSet;

public class Table {
    public String id;
    public HashSet<User> users = new HashSet<>();

    Table(String id) {
        this.id = id;
    }

    Table(String id, User user) {
        this.id = id;
        this.users.add(user);
    }
}
