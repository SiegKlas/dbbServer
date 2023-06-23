package ru.nsu.michael.dbbserver;

import java.util.HashSet;

public class User {
    public Integer id;
    public final HashSet<Session> sessions = new HashSet<>();
    public Boolean isAlive = true;

    public long updatedTime = System.nanoTime();

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, Session session) {
        this.id = id;
        this.sessions.add(session);
    }
}
