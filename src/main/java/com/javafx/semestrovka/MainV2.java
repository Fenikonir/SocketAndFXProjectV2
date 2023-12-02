package com.javafx.semestrovka;

import com.javafx.semestrovka.socket.ChatClient;

public class MainV2 {
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        ChatClient chatClient = new ChatClient();
        chatClient.startConnection("127.0.0.1", 1234);
        chatClient.iAmWhite = false;
        chessBoard.chatClient = chatClient;
        chatClient.chessBoard = chessBoard;
        System.out.println("Player Run");

    }
}
