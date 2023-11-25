package com.javafx.semestrovka.chess;

public class ChessCoordinateConverter {

    public static int[] convertChessToClassic(String chessCoordinate) {
        if (chessCoordinate.length() != 2) {
            throw new IllegalArgumentException("Invalid chess coordinate format");
        }

        char file = chessCoordinate.charAt(0);
        char rank = chessCoordinate.charAt(1);

        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid chess coordinate");
        }

        int row = 7 - (rank - '1');
        int col = file - 'A';

        return new int[]{row, col};
    }

    public static String convertClassicToChess(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Invalid classic coordinates");
        }

        char file = (char) ('A' + col);
        char rank = (char) ('8' - row);

        return "" + file + rank;
    }

    public static void main(String[] args) {
        String[] chessCoordinates = {"A1", "A8", "H1", "H8"};

        for (String chessCoordinate : chessCoordinates) {
            int[] classicCoordinates = convertChessToClassic(chessCoordinate);
            System.out.println(chessCoordinate + " - у меня это row: " + classicCoordinates[0] + " col: " + classicCoordinates[1]);
        }
    }
}

