package com.javafx.semestrovka;

import com.javafx.semestrovka.socket.ChatClient;

public class ChessGame {
    public static void main(String[] args) {

        ChessBoard chessBoard = new ChessBoard();
        ChatClient chatClient = new ChatClient();
        chatClient.iAmWhite = true;
        chatClient.startConnection("127.0.0.1", 1234);
        chatClient.chessBoard = chessBoard;
        chessBoard.chatClient = chatClient;
        System.out.println("Player run");

        // Example: Create a room

        // OR

        // Example: Join an existing room
        // chatClient.joinRoom("Player2");
    }
}
