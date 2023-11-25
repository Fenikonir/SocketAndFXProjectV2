package com.javafx.semestrovka.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Create a new thread to listen for server messages
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Create a new thread to read messages from console and send them to the server
            new Thread(() -> {
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                try {
                    while ((userInput = stdIn.readLine()) != null) {
                        sendMessage(userInput);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
            System.exit(1);
        }
    }

    public void sendMessage(String s) {
        out.println(s);
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String name) {
        this.startConnection("127.0.0.1", 1234);
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.startConnection("127.0.0.1", 1234);
    }
}
