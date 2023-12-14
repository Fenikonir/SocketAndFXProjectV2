package org.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

class ClientHandler extends Thread {
    private Socket clientSocket;
    private UUID uuid = UUID.randomUUID();
    private PrintWriter out;
    private BufferedReader in;
    private String name;

    public void setClients(List<ClientHandler> clients) {
        this.clients = clients;
    }

    private List<ClientHandler> clients;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            name = in.readLine(); // read the name of the client
            broadcast(name + " joined the chat");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(name + ": " + inputLine);
                broadcast(name + ": " + inputLine);
            }

            clients.remove(this);
            broadcast(name + " left the chat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            if (client.uuid != this.uuid) {
                client.out.println(message);
            }
        }
    }
}
