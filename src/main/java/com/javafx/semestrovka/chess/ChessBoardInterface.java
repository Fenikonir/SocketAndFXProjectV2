package com.javafx.semestrovka.chess;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public interface ChessBoardInterface {
    String[][] pieces = new String[8][8];
    ChessMoves chessMoves = null;
    GridPane chessBoard = new GridPane();
    Button[][] squares = new Button[8][8];
    String[] pieceNames = {"R", "N", "B", "Q", "K", "B", "N", "R"};
    String pawnName = "P";
    String sourceRoot = "/com/javafx/semestrovka/alpha/";
    List<int[]> moves = new ArrayList<>();
    List<String> blackPieces = new ArrayList<>();
    List<String> whitePieces = new ArrayList<>();
    int[][] kingCoordinates = new int[2][2];
    boolean nowIsWhite = true;
    int selectedRow = -1;
    int oldSelectedRow = -1;
    int selectedCol = -1;
    int oldSelectedCol = -1;
}
