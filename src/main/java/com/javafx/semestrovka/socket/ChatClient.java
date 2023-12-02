package com.javafx.semestrovka.socket;

import com.javafx.semestrovka.ChessBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClient {
    public boolean iAmWhite = true;
    private Socket clientSocket;
    public ChessBoard chessBoard;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String regex = "([wb]:\\d+:\\d+:\\d+:\\d+)";
            Pattern pattern = Pattern.compile(regex);

            // Create a new thread to listen for server messages
            new Thread(() -> {
                boolean isRun = false;
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        Matcher matcher = pattern.matcher(serverMessage);
                        System.out.println(serverMessage);
                        if (serverMessage.contains("GAME_START") && !isRun) {
                            isRun = true;
                            new Thread(() -> {
                                chessBoard.runGame(iAmWhite);
                            }).start();
                        }else if (matcher.find()) {
                            String matchedSubstring = matcher.group(1); // Получаем подстроку из первой группы
                            String[] parts = matchedSubstring.split(":");

                            if (parts.length == 5) {
                                try {
                                    String color = parts[0];
                                    int selectedRow = Integer.parseInt(parts[1]);
                                    int selectedCol = Integer.parseInt(parts[2]);
                                    int row = Integer.parseInt(parts[3]);
                                    int col = Integer.parseInt(parts[4]);
                                    chessBoard.getMove(color, selectedRow, selectedCol, row, col);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
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
