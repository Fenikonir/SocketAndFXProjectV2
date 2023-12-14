package com.javafx.semestrovka.chess;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class ChessConnection {
    private boolean connectionSuccessful = false;
    private Socket socket;
    private String ip = "127.0.0.1";
    private PrintWriter out;
    private BufferedReader in;
    private int port = 1234;
    public ChessConnection(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }
    public ChessConnection() {}
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    private Socket generateSocket() throws IOException {
        return new Socket(ip, port);
    }
    public Socket getSocket() throws IOException {
        if (socket == null) {
            socket = generateSocket();
        }
        connectionSuccessful = true;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return socket;
    }
    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    public void setConnectionSuccessful(boolean connectionSuccessful) {
        this.connectionSuccessful = connectionSuccessful;
    }public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public static ChessConnection getFirebase() throws IOException, InterruptedException, ExecutionException {
        Task<ChessConnection> backgroundTask = new Task<>() {
            @Override
            protected ChessConnection call() throws Exception {
                try {

                    InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("com/javafx/semestrovka/chess/summerpractick-firebase-adminsdk-9m1sj-e62342d3f5.json");
                    GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccount);
                    GoogleCredentials scoped = credentials.createScoped(
                            Arrays.asList(
                                    "https://www.googleapis.com/auth/firebase.database",
                                    "https://www.googleapis.com/auth/userinfo.email"
                            )
                    );
                    scoped.refreshIfExpired();
                    String token = scoped.getAccessToken().getTokenValue();
                    String firebaseProjectUrl = "https://summerpractick-default-rtdb.europe-west1.firebasedatabase.app/chess.json";

                    URL url = new URL(firebaseProjectUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    // Дополнительные настройки запроса, если необходимо

                    // Чтение ответа
                    ChessConnection chessConnection = new ChessConnection();
                    try (Scanner scanner = new Scanner(connection.getInputStream())) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            Gson gson = new Gson();
                            Map data = gson.fromJson(line, Map.class);
                            String ip = (String) data.get("ip");
                            int port = ((Double) data.get("port")).intValue();
                            chessConnection.setIp(ip);
                            chessConnection.setPort(port);
                            System.out.println(line);
                        }
                        return chessConnection;
                    }
                } catch (IOException e) {
                    // Обработка ошибок
                    throw new Exception("Failed to retrieve data from Firebase", e);
                }
            }
        };

        backgroundTask.setOnSucceeded(event1 -> {
            // Обработка успешного завершения фоновой задачи
            ChessConnection chessConnections = backgroundTask.getValue();
            // Обновите пользовательский интерфейс или выполните другие действия
        });

        backgroundTask.setOnFailed(event1 -> {
            // Обработка ошибок
            Throwable exception = backgroundTask.getException();
            exception.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        });

        new Thread(backgroundTask).start();

        return backgroundTask.get();
    }
}
