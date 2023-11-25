package com.javafx.semestrovka.socket;

public class MainV3 {
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.startConnection("5.tcp.eu.ngrok.io", 10748);
    }
}
