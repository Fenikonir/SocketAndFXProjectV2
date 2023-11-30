package com.javafx.semestrovka;

import com.javafx.semestrovka.chess.ChessBoardInterface;
import com.javafx.semestrovka.chess.ChessMoves;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.javafx.semestrovka.chess.ChessMoves.isCheckByKing;

public class ChessBoard extends Application {
    public static String[][] pieces = new String[8][8];
    private final ChessMoves chessMoves = null;
    private GridPane chessBoard;
    private static final Button[][] squares = new Button[8][8];
    private static final String[] pieceNames = {"R", "N", "B", "Q", "K", "B", "N", "R"};
    private static final String pawnName = "P";
    private static final String sourceRoot = "/com/javafx/semestrovka/alpha/";

    private List<int[]> moves;
    private static final List<String> blackPieces = new ArrayList<>();
    private static final List<String> whitePieces = new ArrayList<>();
    private static int[][] kingCoordinates = new int[2][2];
    private boolean nowIsWhite = true;

    private int selectedRow = -1;
    private int oldSelectedRow = -1;
    private int selectedCol = -1;
    private int oldSelectedCol = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chess Board");

        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(10));

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
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();
    }

    private void addChessPieces(boolean isWhiteOnBottom) {
        for (int col = 0; col < 8; col++) {
            Image pieceImage;
            if (isWhiteOnBottom) {
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "w" + pieceNames[col] + ".png")));
                squares[7][col].setGraphic(new ImageView(pieceImage));
                pieces[7][col] = "w" + pieceNames[col];
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "w" + pawnName + ".png")));
                squares[6][col].setGraphic(new ImageView(pieceImage));
                pieces[6][col] = "w" + pawnName;
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "b" + pieceNames[col] + ".png")));
                squares[0][col].setGraphic(new ImageView(pieceImage));
                if (pieceNames[col].equals("K")) {
                    kingCoordinates[0][0] = 7;
                    kingCoordinates[0][1] = col;
                    kingCoordinates[1][0] = 0;
                    kingCoordinates[1][1] = col;
                }
                pieces[0][col] = "b" + pieceNames[col];
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "b" + pawnName + ".png")));
                squares[1][col].setGraphic(new ImageView(pieceImage));
                pieces[1][col] = "b" + pawnName;
            } else {
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "b" + pieceNames[col] + ".png")));
                squares[7][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "b" + pawnName + ".png")));
                squares[6][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "w" + pieceNames[col] + ".png")));
                squares[0][col].setGraphic(new ImageView(pieceImage));
                pieceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sourceRoot + "w" + pawnName + ".png")));
                squares[1][col].setGraphic(new ImageView(pieceImage));
            }
        }
    }


    private void handleSquareClick(int row, int col) {
        if (selectedRow == -1 && selectedCol == -1) {
            if (pieces[row][col] != null) {
                boolean isWhite = isWhitePiece(row, col);
                if ((nowIsWhite && isWhite) || (!nowIsWhite && !isWhite)) {
                    selectedRow = row;
                    selectedCol = col;

                    String type = pieces[row][col];
                    moves = ChessMoves.getUniversalMoves(type, row, col);
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
                    isEaten(row, col);
                    placePiece(row, col);
                    pieces[row][col] = pieces[selectedRow][selectedCol];
                    nowCheck(row, col);
                    pieces[selectedRow][selectedCol] = null;
                    colored(false, row, col);
                    nowIsWhite = !nowIsWhite;
                    clearSelection();
                    moves.clear();
                    break;
                }
            }
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

    private void colored(boolean isBefore, int rows, int cols) {
        int selectedSum = selectedRow + selectedCol;
        if (isBefore) {
            squares[selectedRow][selectedCol].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: #f7baba;" : "-fx-background-color: #ad8292;");
            squares[oldSelectedCol][oldSelectedRow].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: grey;");
        } else {
            squares[selectedRow][selectedCol].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: grey;");
            squares[rows][cols].setStyle(selectedSum % 2 == 0 ? "-fx-background-color: #e3f0fc;" : "-fx-background-color: #677582;");
        }

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
    }

    private void nowCheck(int row, int col) {
        String selestPiece = pieces[row][col];
        boolean isKing = pieces[row][col].contains("K");
        boolean isCheck = isKing ? isCheckByKing(selestPiece, row, col) : ChessMoves.isCheckCurrentPiece(selestPiece, row, col);
        if (isCheck) {
            String color = isKing ? (nowIsWhite ? "Белому" : "Черному") : (!nowIsWhite ? "Белому" : "Черному");
            showAlert("Шах!", color + " Королю поставлен шах!");
        } else if (nowIsWhite) {
            isCheck = isCheckByKing("wK", kingCoordinates[0][0], kingCoordinates[0][1]);
            if (isCheck) {
                showAlert("Шах!", "Черному Королю поставлен шах!");
            }
        } else if (!nowIsWhite) {
            isCheck = isCheckByKing("bK", kingCoordinates[1][0], kingCoordinates[1][1]);
            if (isCheck) {
                showAlert("Шах!", "Белому Королю поставлен шах!");
            }
        }
    }

    private void placePiece(int targetRow, int targetCol) {
        // Move the piece to the new cell
        ImageView pieceImageView = (ImageView) squares[selectedRow][selectedCol].getGraphic();
        squares[targetRow][targetCol].setGraphic(pieceImageView);
        squares[selectedRow][selectedCol].setGraphic(null); // Clear the old cell
    }

    public static boolean canCastleKingside(int row, int col) {
        // Check if kingside castling is possible
        return isWhitePiece(row, col) && !isOccupied(row, col + 1) && !isOccupied(row, col + 2)
                && pieces[row][7] != null && pieces[row][7].equals("wR") /* Check if the rook has not moved */;
    }

    public static boolean canCastleQueenside(int row, int col) {
        // Check if queenside castling is possible
        return isWhitePiece(row, col) && !isOccupied(row, col - 1) && !isOccupied(row, col - 2) && !isOccupied(row, col - 3)
                && pieces[row][0] != null && pieces[row][0].equals("wR") /* Check if the rook has not moved */;
    }


    private void clearSelection() {
        oldSelectedRow = selectedRow;
        oldSelectedCol = selectedCol;
        selectedRow = -1;
        selectedCol = -1;
    }

    public static boolean isOccupied(int row, int col) {
        return pieces[row][col] != null;
    }

    public static boolean isWhitePiece(int row, int col) {
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
