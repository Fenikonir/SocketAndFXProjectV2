package com.javafx.semestrovka.socket;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Room {
    private int code;
    private ClientHandler creator;
    private ClientHandler joiner;
    private CountDownLatch gameStartLatch;

    public Room(int code, ClientHandler creator) {
        this.code = code;
        this.creator = creator;
        this.gameStartLatch = new CountDownLatch(2); // Initialize with the number of players needed
    }

    public Room() {
        this.gameStartLatch = new CountDownLatch(2); // Initialize in the default constructor as well
    }

    public int getCode() {
        return code;
    }

    public ClientHandler getCreator() {
        return creator;
    }

    public ClientHandler getJoiner() {
        return joiner;
    }

    public void setCreator(ClientHandler creator) {
        this.creator = creator;
    }

    public void setJoiner(ClientHandler joiner) {
        this.joiner = joiner;
    }

    public void addJoiner(ClientHandler clientHandler) {
        if (joiner == null) {
            joiner = clientHandler;
        }
    }

    public List<ClientHandler> getClientHandlers() {
        if (joiner == null) {
            return Collections.singletonList(creator);
        }
        return List.of(creator, joiner);
    }

    public void startGame() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);

        for (ClientHandler handler : getClientHandlers()) {
            handler.broadcast("GAME_START");
        }
    }

    public boolean isOpened() {
        return joiner == null;
    }

    public boolean tryAddJoiner(ClientHandler clientHandler) {
        if (joiner == null) {
            joiner = clientHandler;
            creator.setClients(List.of(creator, joiner));
            joiner.setClients(List.of(creator, joiner));
            gameStartLatch.countDown(); // Уменьшим счетчик игроков, которые присоединились
            return true;
        }
        return false;
    }
}
