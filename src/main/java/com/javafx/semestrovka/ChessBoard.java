package com.javafx.semestrovka;

import com.google.gson.Gson;
import com.javafx.semestrovka.chess.ChessBoardInterface;
import com.javafx.semestrovka.chess.ChessMoves;
import com.javafx.semestrovka.chess.ChessConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChessBoard extends Application implements ChessBoardInterface {
    public ChessBoard(String result, String skin) {
        this.result = result;
        sourceRootV2 = String.format(sourceRoot, skin);
    }
    private ChessConnection chessConnection;
    private Stage gameStage = null;
    private String result;
    private boolean gameStarted = false;
    public String name = "Player";
    private final String sourceRootV2;
    boolean iAmWhite;
    boolean nowIsWhite = true;
    public String[][] pieces = new String[8][8];
    ChessMoves chessMoves = null;
    GridPane chessBoard = new GridPane();
    Button[][] squares = new Button[8][8];

    List<int[]> moves = new ArrayList<>();
    List<String> blackPieces = new ArrayList<>();
    List<String> whitePieces = new ArrayList<>();
    int[][] kingCoordinates = new int[2][2];
    int selectedRow = -1;
    String color;
    int oldStartRow = -1;
    int oldEndRow = -1;
    int selectedCol = -1;
    int oldStartCol = -1;
    int oldEndCol = -1;
    public static void main(String[] args) {
        launch(args);
    }

    private void connectToServer() {
        try {
            chessConnection = ChessConnection.getFirebase();
//            chessConnection = new ChessConnection();
            chessConnection.getSocket();

        } catch (ConnectException connectException) {
            chessConnection.setConnectionSuccessful(false);
            showAlert("Шоколадки", "Нет соединения с сервером");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToServer(String message) {
        chessConnection.getOut().println(message);
    }

    private void receiveMessages() {
        String regex = "([wb]:\\d+:\\d+:\\d+:\\d+)";
        Pattern pattern = Pattern.compile(regex);
        try {
            String message;
            try {
                while (!chessConnection.getSocket().isClosed() && (message = chessConnection.getIn().readLine()) != null) {
                    System.out.println("Received from server: " + message);
                    Matcher matcher = pattern.matcher(message);

                    if (matcher.find()) {
                        String matchedSubstring = matcher.group(1); // Получаем подстроку из первой группы
                        String[] parts = matchedSubstring.split(":");

                        if (parts.length == 5) {
                            try {
                                String color = parts[0];
                                int selectedRow = Integer.parseInt(parts[1]);
                                int selectedCol = Integer.parseInt(parts[2]);
                                int row = Integer.parseInt(parts[3]);
                                int col = Integer.parseInt(parts[4]);
                                getMove(color, selectedRow, selectedCol, row, col);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                if (e.getMessage().equals("Socket closed")) {
                    // Handle socket closure, e.g., break out of the loop or perform cleanup
                } else {
                    // Handle other SocketException scenarios
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (chessConnection.getSocket() != null && !chessConnection.getSocket().isClosed()) {
                chessConnection.getSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        if (result != null) {
            System.out.println(result);
        }

        connectToServer();

        // Wait for "START_GAME" signal from the server
        if (chessConnection.isConnectionSuccessful()) {
            new Thread(() -> {
                try {
                    String serverMessage;
                    chessConnection.getOut().println(result);
                    while ((serverMessage = chessConnection.getIn().readLine()) != null) {
//                    if ("GAME_START".equals(serverMessage)) {
//                        gameStarted = true;
//                        sendMessageToServer("Player");
//                        Platform.runLater(this::startGame);
//                        break;
//                    }
                        if (serverMessage.contains("GAME_START")) {
                            char lastChar = serverMessage.charAt(serverMessage.length() - 1);
                            System.out.println("Last character: " + lastChar);
                            gameStarted = true;
                            sendMessageToServer("Player");
                            boolean c = 'B' != lastChar;
//                        Platform.runLater(() -> startGame(c));
                            Platform.runLater(() -> startGame(c));
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void startGame(boolean wB) {
        if (gameStarted) {
            System.out.println("Result: " + result);
            this.iAmWhite = wB;
            gameStage = new Stage();
            chessMoves = new ChessMoves(this);

            System.setProperty("console.encoding", "UTF-8");
            gameStage.setTitle("Chess Board");
            chessBoard.setPadding(new Insets(10));
            color = "b";
            if (iAmWhite) {
                color = "w";
            }
            // Add empty cell in the top-left corner
            chessBoard.add(new Label(), 0, 0);

            // Add letters for horizontal coordinates
            for (int col = 0; col < 8; col++) {
                Button label = new Button(Character.toString((char) ('A' + col)));
                label.setDisable(true);
                chessBoard.add(label, col + 1, 0);
            }

            // Add numbers for vertical coordinates and chessboard cells
            for (int row = 0; row < 8; row++) {
                Button numberLabel = new Button(Integer.toString(8 - row));
                numberLabel.setDisable(true);
                chessBoard.add(numberLabel, 0, row + 1);

                for (int col = 0; col < 8; col++) {
                    squares[row][col] = new Button();
                    squares[row][col].setMinSize(80, 80);
                    squares[row][col].setPrefSize(80, 80);
                    squares[row][col].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    squares[row][col].setStyle("-fx-background-color: " + ((row + col) % 2 == 0 ? "white;" : "grey;") + "-fx-border-width: 1; -fx-border-color: black;");
                    if ((row + col) % 2 == 0) {
                        squares[row][col].setStyle("-fx-background-color: white;");
                    } else {
                        squares[row][col].setStyle("-fx-background-color: grey;");
                    }
                    int finalRow = row;
                    int finalCol = col;
                    squares[row][col].setOnMouseClicked(e -> handleSquareClick(finalRow, finalCol));
                    chessBoard.add(squares[row][col], col + 1, row + 1);
                }
            }

            addChessPieces(true); // true - white pieces at the bottom, false - black pieces at the bottom

            Scene scene = new Scene(chessBoard, 720, 720);
            gameStage.setScene(scene);
            gameStage.setResizable(false);
            gameStage.setOnCloseRequest(event -> {
                closeConnection();
                Platform.exit();
                System.out.println("Приложение закрывается");
            });
            gameStage.show();
            new Thread(this::receiveMessages).start();
//            chatClient.sendMessage(name);
        }
    }

    private void addChessPieces(boolean isWhiteOnBottom) {
        for (int col = 0; col < 8; col++) {
            Image pieceImage;
            if (isWhiteOnBottom) {
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "w" + pieceNames[col] + ".png")));
                squares[7][col].setGraphic(new ImageView(pieceImage));
                pieces[7][col] = "w" + pieceNames[col];
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "w" + pawnName + ".png")));
                squares[6][col].setGraphic(new ImageView(pieceImage));
                pieces[6][col] = "w" + pawnName;
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "b" + pieceNames[col] + ".png")));
                squares[0][col].setGraphic(new ImageView(pieceImage));
                if (pieceNames[col].equals("K")) {
                    kingCoordinates[0][0] = 7;
                    kingCoordinates[0][1] = col;
                    kingCoordinates[1][0] = 0;
                    kingCoordinates[1][1] = col;
                }
                pieces[0][col] = "b" + pieceNames[col];
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "b" + pawnName + ".png")));
                squares[1][col].setGraphic(new ImageView(pieceImage));
                pieces[1][col] = "b" + pawnName;
            } else {
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "b" + pieceNames[col] + ".png")));
                squares[7][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "b" + pawnName + ".png")));
                squares[6][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "w" + pieceNames[col] + ".png")));
                squares[0][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRootV2 + "w" + pawnName + ".png")));
                squares[1][col].setGraphic(new ImageView(pieceImage));
            }
        }
    }


    private void handleSquareClick(int row, int col) {
        if (iAmWhite == nowIsWhite) {
            if (selectedRow == -1 && selectedCol == -1) {
                if (pieces[row][col] != null) {
                    boolean isWhite = isWhitePiece(row, col);
                    if ((nowIsWhite && isWhite) || (!nowIsWhite && !isWhite)) {
                        selectedRow = row;
                        selectedCol = col;

                        String type = pieces[row][col];
                        moves = chessMoves.getUniversalMoves(type, row, col);
                        assert type != null;
                        moveValidate(type.contains("w"));
                        colored(true, 0, 0);
                        if (moves.isEmpty()) {
                            clearSelection();
                        }
                    }
                }
            } else {
                // Second click: select target square
                for (int[] coordinates : moves) {
                    if (coordinates[0] == row && coordinates[1] == col) {
                        makeMove(row, col, true);
                        break;
                    }
                }
            }
        }
    }

    private void makeMove(int row, int col, boolean local) {
        isEaten(row, col);
        placePiece(row, col, local);
        colored(false, row, col);
        nowCheck(row, col, local);
        nowIsWhite = !nowIsWhite;
        oldStartRow = selectedRow;
        oldStartCol = selectedCol;
        oldEndCol = col;
        oldEndRow = row;
        System.out.println("Ход из клетки: " + convertToChessNotation(selectedRow, selectedCol) +
                ", на клетку: " + convertToChessNotation(row, col));
        if (local) {
            sendMove(selectedRow, selectedCol, row, col);
        }
        clearSelection();
        moves.clear();
        if (iAmWhite == nowIsWhite) {
            gameStage.setTitle("Шахматы: Ваш ход");
        } else {
            gameStage.setTitle("Шахматы: Ход соперника");
        }
    }

    private void moveValidate(boolean isWhite) {
        List<int[]> validatedMoves = new ArrayList<>();
        for (int[] coordinates : moves) {
            String pieceType = pieces[coordinates[0]][coordinates[1]];
            if (pieceType != null) {
                if (isWhite && pieceType.contains("b") || !isWhite && pieceType.contains("w")) {
                    validatedMoves.add(coordinates);
                }
            } else {
                validatedMoves.add(coordinates);
            }
        }
        moves = validatedMoves;
    }

    private void isEaten(int row, int col) {
        String pieceType = pieces[row][col];
        if (pieceType != null) {
            if (nowIsWhite) {
                blackPieces.remove(pieceType);
            } else {
                whitePieces.remove(pieceType);
            }
            System.out.println(pieceType + " was eaten " + whitePieces.size() + " " + blackPieces.size());
        }
    }

    private void colored(boolean isBefore, int rowN, int colN) {
        int selectedSum = selectedRow + selectedCol;
        //Окрашивает выбранную клетку - а так же старый ход обнуляет, новый ход закрышивает;
        if (isBefore) {
            squares[selectedRow][selectedCol].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: #f7baba;" : "-fx-background-color: #ad8292;");
        } else {
            squares[selectedRow][selectedCol].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: #c2daf0;" : "-fx-background-color: #516373;");
            if (oldEndRow != -1 && oldStartRow != -1) {
                squares[oldStartRow][oldStartCol].setStyle((oldStartRow + oldStartCol) % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: grey;");
                squares[oldEndRow][oldEndCol].setStyle(((oldEndRow + oldEndCol)) % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: grey;");
            }
        }

        //Окрашивает возможные ходы
        for (int[] coordinates : moves) {
            int row = coordinates[0];
            int col = coordinates[1];
            int sum = row + col;
            if (isBefore) {
                squares[row][col].setStyle(sum % 2 == 0 ? "-fx-background-color: #ffe1a8;" : "-fx-background-color: #ffa600;");
            } else {
                squares[row][col].setStyle(sum % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: grey;");
            }
        }
        if (!isBefore) {
            squares[rowN][colN].setStyle((rowN + colN) % 2 == 0 ? "-fx-background-color: #c2daf0;" : "-fx-background-color: #516373;");
        }
    }

    private boolean nowCheck(int row, int col, boolean local) {
        String selestPiece = pieces[row][col];
        boolean isKing = pieces[row][col].contains("K");
        boolean isCheck = isKing ? chessMoves.isCheckByKing(selestPiece, row, col) : chessMoves.isCheckCurrentPiece(selestPiece, row, col);
        if (isCheck) {
            String color = isKing ? (nowIsWhite ? "Белому" : "Черному") : (!nowIsWhite ? "Белому" : "Черному");
            if (local) {
                showAlert("Шах!", color + " Королю поставлен шах!");
            } else {
//                Platform.runLater(() -> {
                    showAlert("Шах!", color + " Королю поставлен шах!");
//                });
            }
        }
//        else if (nowIsWhite) {
//            isCheck = isCheckByKing("wK", kingCoordinates[0][0], kingCoordinates[0][1]);
//            if (isCheck) {
//                showAlert("Шах!", "Черному Королю поставлен шах!");
//            }
//        } else if (!nowIsWhite) {
//            isCheck = isCheckByKing("bK", kingCoordinates[1][0], kingCoordinates[1][1]);
//            if (isCheck) {
//                showAlert("Шах!", "Белому Королю поставлен шах!");
//            }
//        }
        return isCheck;
    }

    private void placePiece(int targetRow, int targetCol, boolean local) {
        // Move the piece to the new cell
        ImageView pieceImageView = (ImageView) squares[selectedRow][selectedCol].getGraphic();
        pieces[targetRow][targetCol] = pieces[selectedRow][selectedCol];
        if (local) {
            squares[targetRow][targetCol].setGraphic(pieceImageView);
            squares[selectedRow][selectedCol].setGraphic(null); // Clear the old cell
        } else {
//            Platform.runLater(() -> {
                squares[targetRow][targetCol].setGraphic(pieceImageView);
                squares[selectedRow][selectedCol].setGraphic(null); // Clear the old cell
//            });
        }
        pieces[selectedRow][selectedCol] = null;
    }

    public boolean canCastleKingside(int row, int col) {
        // Check if kingside castling is possible
        return isWhitePiece(row, col) && !isOccupied(row, col + 1) && !isOccupied(row, col + 2)
                && pieces[row][7] != null && pieces[row][7].equals("wR") /* Check if the rook has not moved */;
    }

    public boolean canCastleQueenside(int row, int col) {
        // Check if queenside castling is possible
        return isWhitePiece(row, col) && !isOccupied(row, col - 1) && !isOccupied(row, col - 2) && !isOccupied(row, col - 3)
                && pieces[row][0] != null && pieces[row][0].equals("wR") /* Check if the rook has not moved */;
    }

    private String convertToChessNotation(int row, int col) {
        char letter = (char) ('A' + col);
        int number = 8 - row;
        return String.valueOf(letter) + number;
    }
    private void sendMove(int selectedRow, int selectedCol, int row, int col) {
        sendMessageToServer(String.format("%s:%d:%d:%d:%d", color, selectedRow, selectedCol, row, col));
    }
    public void getMove(String color, int selectedRow, int selectedCol, int row, int col) {
        if (!this.color.equals(color)) {
            this.selectedRow = selectedRow;
            this.selectedCol = selectedCol;
            Platform.runLater(() -> {
                makeMove(row, col, false);
            });
        }
    }


    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
    }

    public boolean isOccupied(int row, int col) {
        return pieces[row][col] != null;
    }

    public boolean isWhitePiece(int row, int col) {
        if (isOccupied(row, col)) {
            return pieces[row][col].contains("w");
        }
        return false;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
