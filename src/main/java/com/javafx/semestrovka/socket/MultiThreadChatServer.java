package com.javafx.semestrovka.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Chat server started on port 1234");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                boolean f = false;
                while ((inputLine = in.readLine()) != null) {
                    f = parseString(inputLine);
                    break;
                }
                if (!f) {

                    boolean added = false;

                    for (Room room : rooms) {
                        if (room.isOpened()) {
                            added = true;
                            ClientHandler clientHandler = new ClientHandler(clientSocket, room.getClientHandlers());
                            room.addJoiner(clientHandler);
                            clients.add(clientHandler);
                            room.getCreator().setClients(List.of(room.getCreator(), room.getJoiner()));
                            room.getJoiner().setClients(List.of(room.getCreator(), room.getJoiner()));
                            clientHandler.start();
                            room.startGame();
                        }
                    }
                    if (!added) {
                        createRoom(clientSocket);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Room createRoom(Socket clientSocket) {
        Room room = new Room();
        ClientHandler clientHandler = new ClientHandler(clientSocket, null);
        room.setCreator(clientHandler);
        clientHandler.setClients(List.of(clientHandler));
        clients.add(clientHandler);
        clientHandler.start();
        rooms.add(room);
        return room;
    }

    private static boolean parseString(String input) {
        if (input.contains("true")){
            return true;
        }
        return false;
//        String[] parts = input.split(":");
//        if (parts.length == 4) {
//            return parts;
//        } else if (parts.length == 3) {
//            return false; // Неправильный формат строки
//        }
    }
}