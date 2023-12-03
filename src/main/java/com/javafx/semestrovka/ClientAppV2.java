package com.javafx.semestrovka;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAppV2 extends Application {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private boolean gameStarted = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        connectToServer();

        // Wait for "START_GAME" signal from the server
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    if ("GAME_START".equals(serverMessage)) {
                        gameStarted = true;
                        Platform.runLater(this::openGameWindow);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 1234); // Change to your server's address and port
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGameWindow() {
        if (gameStarted) {
            Stage gameStage = new Stage();
            gameStage.setTitle("Game Window");
            VBox root = new VBox(10);

            TextField messageField = new TextField();
            Button sendButton = new Button("Send");

            sendButton.setOnAction(event -> {
                String message = messageField.getText();
                sendMessageToServer(message);
                messageField.clear();
            });

            root.getChildren().addAll(messageField, sendButton);

            Scene scene = new Scene(root, 300, 200);
            gameStage.setScene(scene);
            gameStage.setOnCloseRequest(event -> {
                closeConnection();
                Platform.exit();
            });

            gameStage.show();

            // Listen for messages from the server
            new Thread(this::receiveMessages).start();
        }
    }

    private void sendMessageToServer(String message) {
        out.println(message);
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from server: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
