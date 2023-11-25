package com.javafx.semestrovka.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

    public EchoClient() throws IOException {
        setup();
        System.out.println(this.sendMessage("start"));
    }

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        readMessage();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void setup() throws IOException {
        this.startConnection("127.0.0.1", 9999);
    }

    public void tearDown() throws IOException {
        this.stopConnection();
    }

    public void readMessage() throws IOException {
        String serverResponse = in.readLine();
        if (serverResponse != null) {
            System.out.println("Server: " + serverResponse);
        }
    }
}