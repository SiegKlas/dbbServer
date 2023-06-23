package ru.nsu.michael.dbbserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
public class DbbServerApplication {
    private static Integer userValue = -1;

    public static final HashSet<Session> sessions = new HashSet<>();

    public static final HashSet<User> users = new HashSet<>();

    public static final HashSet<Table> tables = new HashSet<>();

    private static User generateNewUser() {
        userValue++;
        return new User(userValue);
    }

    private static void addSessionUser(String session, User user) {
        Session currentSession = sessions.stream().filter(s -> s.id.equals(session)).findFirst().orElse(null);
        if (currentSession != null) {
            currentSession.users.add(user);
        } else {
            currentSession = new Session(session, user);
            sessions.add(currentSession);
        }
        user.sessions.add(currentSession);
        users.add(user);
        user.updatedTime = System.nanoTime();
    }

    @RestController
    public static class MyController {

        @GetMapping("/reg_user")
        public Integer registerUser(@RequestParam String session) {
            User user = generateNewUser();
            addSessionUser(session, user);
            user.updatedTime = System.nanoTime();
            return user.id;
        }

        @GetMapping("remove_user")
        public void removeUser(@RequestParam Integer user) {
            User currentUser = users.stream().filter(u -> u.id.equals(user)).findFirst().orElse(null);
            if (currentUser == null) {
                return;
            }
            users.remove(currentUser);
            for (Session session : sessions) {
                session.users.removeIf(u -> u == currentUser);
            }
            for (Table table : tables) {
                table.users.removeIf(u -> u == currentUser);
            }
        }

        @GetMapping("/sessions")
        public List<String> getSessions(@RequestParam Integer user) {
            System.out.println("getSession");
            users.forEach(u -> u.sessions.forEach(System.out::println));
            return users.stream().filter(u -> u.id.equals(user)).findFirst().map(u -> u.sessions.stream().map(s -> s.id).toList()).orElse(null);
        }

        @GetMapping("/users_count")
        public Integer getUsersCountForTable(@RequestParam String table) {
            Table currentTable = tables.stream().filter(t -> t.id.equals(table)).findFirst().orElse(null);
            if (currentTable != null) {
                return currentTable.users.size();
            } else {
                return -1;
            }
        }

        @GetMapping("/user_table")
        public void updateTable(@RequestParam Integer user, @RequestParam String table) {
            User currentUser = users.stream().filter(u -> u.id.equals(user)).findFirst().orElse(null);
            if (currentUser == null) {
                return;
            }
            Table currentTable = tables.stream().filter(t -> t.id.equals(table)).findFirst().orElse(new Table(table));
            currentTable.users.add(currentUser);
            tables.add(currentTable);
            currentUser.updatedTime = System.nanoTime();
        }


        @GetMapping("/add_session")
        public Integer updateUser(@RequestParam Integer user, @RequestParam String session) {
            User currentUser = users.stream().filter(u -> u.id.equals(user)).findFirst().orElse(null);
            if (currentUser == null) {
                var id = registerUser(session);
                currentUser = users.stream().filter(u -> u.id.equals(id)).findFirst().orElse(null);
            }
            assert currentUser != null;
            Session currentSession = sessions.stream().filter(s -> s.id.equals(session)).findFirst().orElse(new Session(session, currentUser));
            currentSession.users.add(currentUser);
            currentUser.sessions.add(currentSession);
            sessions.add(currentSession);
            currentUser.updatedTime = System.nanoTime();
            return currentUser.id;
        }

        @GetMapping("/sessions_count")
        public Integer getSessionsCountForUser(@RequestParam Integer user) {
            User currentUser = users.stream().filter(u -> u.id.equals(user)).findFirst().orElse(null);
            if (currentUser != null) {
                return currentUser.sessions.size();
            } else {
                return -1;
            }
        }

        @GetMapping("/users_session_count")
        public Integer getUsersCountForSession(@RequestParam String session) {
            Session currentSession = sessions.stream().filter(s -> s.id.equals(session)).findFirst().orElse(null);
            if (currentSession != null) {
                return currentSession.users.size();
            } else {
                return -1;
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DbbServerApplication.class, args);
        Thread thread = new CleanerThread();
        thread.start();
    }

}
