package ru.nsu.michael.dbbserver;

import static ru.nsu.michael.dbbserver.DbbServerApplication.*;

public class CleanerThread extends Thread {
    @Override
    public void run() {
        while (true) {
            for (User currentUser : users) {
                if ((System.nanoTime() - currentUser.updatedTime) / 1000000000.0 > 200000) {
                    users.remove(currentUser);
                    for (Session session : sessions) {
                        session.users.removeIf(u -> u == currentUser);
                    }
                    for (Table table : tables) {
                        table.users.removeIf(u -> u == currentUser);
                    }
                }
            }

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
