package com.javafx.semestrovka.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EchoServer {
    private ServerSocket serverSocket;
    private Map<Socket, PrintWriter> clientSockets = new HashMap<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new ClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            clientSockets.put(clientSocket, out);
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("good bye");
                        broadcast(inputLine);
                        break;
                    } else if ("start".equals(inputLine)) {
                        out.println(generateRandomString(8));
                        broadcast(inputLine);
                    } else {
                        broadcast(inputLine);
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String message) {
            for (PrintWriter out : clientSockets.values()) {
                out.println(message);
            }
        }
    }

    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        EchoServer server = new EchoServer();
        server.start(9999);
    }
}