package com.javafx.semestrovka.chess;

import com.javafx.semestrovka.ChessBoard;

import java.util.ArrayList;
import java.util.List;

public class ChessMoves {
    private ChessBoard chessBoard;
    private int checkRow;
    private int checkCol;
    public ChessMoves(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }
    public String[][] dream = null;
    public List<int[]> getUniversalMoves(boolean need, String[][] pieces, String type, int row, int col) {
        if ("bP".equals(type)) {
            return getPawnMoves(need,pieces, row, col, false);
        } else if ("wP".equals(type)) {
            return getPawnMoves(need,pieces, row, col, true);
        } else if (type.contains("N")) {
            return getKnightMoves(need,row, col);
        } else if (type.contains("B")) {
            return getBishopMoves(need,pieces, row, col);
        } else if (type.contains("R")) {
            return getRookMoves(need,pieces, row, col);
        } else if (type.contains("Q")) {
            return getQueenMoves(need,pieces, row, col);
        } else if (type.contains("K")) {
            return getKingMoves(need,pieces, row, col);
        }

        throw new IllegalArgumentException("Unknown piece type: " + type);
    }

    public List<int[]> getPawnMoves(Boolean need,String[][] pieces, int row, int col, boolean isWhite) {
        List<int[]> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;

        // Вперед
        int newRow = row + direction;
        if (isValidSquare(newRow, col) && !isOccupied(pieces, newRow, col)) {
            moves.add(new int[]{newRow, col});
        }

        // Вперед на две клетки (для начального хода)
        int initialRow = isWhite ? 6 : 1;
        if (row == initialRow) {
            int doubleMoveRow = row + 2 * direction;
            if (isValidSquare(doubleMoveRow, col) && !isOccupied(pieces, doubleMoveRow, col)) {
                moves.add(new int[]{doubleMoveRow, col});
            }
        }

        // Диагональные атаки
        int[] attackCols = {col - 1, col + 1};
        for (int attackCol : attackCols) {
            if (isValidSquare(newRow, attackCol) && isOccupied(pieces, newRow, attackCol) && isOpponentPiece(pieces, row, col, newRow, attackCol)) {
                moves.add(new int[]{newRow, attackCol});
            }
        }
        return movesValidateOnCheck(need, row, col, moves);
    }

    private boolean isOpponentPiece(String[][] pieces, int row, int col, int targetRow, int targetCol) {
        // Проверка, является ли фигура на заданных координатах противниковой
        boolean isWhite = isWhitePiece(pieces, row, col);
        return isWhite != isWhitePiece(pieces, targetRow, targetCol);
    }


    public List<int[]> getKnightMoves(boolean need,int row, int col) {
        List<int[]> moves = new ArrayList<>();

        int[][] knightMoves = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidSquare(newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return movesValidateOnCheck(need, row, col, moves);
    }

    public List<int[]> getRookMoves(boolean need,String[][] pieces, int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Вертикаль и горизонталь
        int[][] rookMoves = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        for (int[] move : rookMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            while (isValidSquare(newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
                if (isOccupied(pieces, newRow, newCol)) {
                    break; // Stop if there is a piece in the way
                }
                newRow += move[0];
                newCol += move[1];
            }
        }

        return movesValidateOnCheck(need, row, col, moves);
    }

    public List<int[]> getBishopMoves(boolean need,String[][] pieces, int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Диагонали
        int[][] bishopMoves = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };

        for (int[] move : bishopMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            while (isValidSquare(newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
                if (isOccupied(pieces, newRow, newCol)) {
                    break; // Stop if there is a piece in the way
                }
                newRow += move[0];
                newCol += move[1];
            }
        }

        return movesValidateOnCheck(need, row, col, moves);
    }


    public boolean isOccupied(String[][] pieces, int row, int col) {
        return pieces[row][col] != null;
    }

    public boolean isWhitePiece(String[][] pieces, int row, int col) {
        if (isOccupied(pieces, row, col)) {
            return pieces[row][col].contains("w");
        }
        return false;
    }


    public List<int[]> getQueenMoves(boolean need,String[][] pieces, int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Комбинация ходов слона и ладьи
        moves.addAll(getBishopMoves(need,pieces, row, col));
        moves.addAll(getRookMoves(need,pieces, row, col));

        return movesValidateOnCheck(need, row, col, moves);
    }
    public int[] isCheckByKing(boolean need, String[][] pieces, String type, int row, int col) {
        // Find the opponent's moves
        boolean isWhite = isWhitePiece(pieces, row, col);
        List<int[]> opponentMoves = getAllMoves(need, pieces, !isWhite);

        // Check if any opponent's move targets the king's position
        for (int[] move : opponentMoves) {
            if (move[0] == row && move[1] == col) {
                System.out.println(type + " is in check!");
                checkRow = move[3];
                checkCol = move[4];
                return new int[]{1, move[3], move[4]};
            }
        }

        return new int[]{0};
    }
    public List<int[]> getKingMoves(boolean need,String[][] pieces, int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Все возможные соседние клетки
        int[][] kingMoves = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] move : kingMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidSquare(newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        // Castling
        if (chessBoard.canCastleKingside(row, col)) {
            moves.add(new int[]{row, col + 2});
        }

        if (chessBoard.canCastleQueenside(row, col)) {
            moves.add(new int[]{row, col - 2});
        }


        return movesValidateOnCheck(need, row, col, moves);
    }

    public List<int[]> moveValidate(List<int[]> moves, boolean isWhite) {
        List<int[]> validatedMoves = new ArrayList<>();
        for (int[] coordinates : moves) {
            String pieceType = chessBoard.pieces[coordinates[0]][coordinates[1]];
            if (pieceType != null) {
                if (isWhite && pieceType.contains("b") || !isWhite && pieceType.contains("w")) {
                    validatedMoves.add(coordinates);
                }
            } else {
                validatedMoves.add(coordinates);
            }
        }
//        if (!validatedMoves.isEmpty()) {
//            return movesValidateOnCheck(false, validatedMoves, );
//        }
        return validatedMoves;
    }

    private boolean isValidSquare(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private List<int[]> getAllMoves(boolean need, String[][] pieces, boolean isWhite) {
        List<int[]> allMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {
                if (isOccupied(pieces, row, col) && isWhitePiece(pieces, row, col) == isWhite) {
                    String pieceType = pieces[row][col];
                    for (int[] i: getUniversalMoves( need, pieces, pieceType, row, col)) {
                        allMoves.add(new int[]{i[0], i[1], pieceType.contains("k")? 1 : 0, row, col});
                    }
                }
            }
        }
        return allMoves;
    }

    public boolean[] isCheck(boolean need, String[][] pieces) {
        int[] whiteKingPosition = findKingPosition(pieces, true);
        int[] blackKingPosition = findKingPosition(pieces, false);
        if (whiteKingPosition == null || blackKingPosition == null) {
            return new boolean[]{false, false};
        }
        if (isCheckByKing(need, pieces, "wK", whiteKingPosition[0], whiteKingPosition[1])[0] == 1) {
            return new boolean[]{true, true};
        } else if (isCheckByKing(need, pieces, "bK", blackKingPosition[0], blackKingPosition[1])[0] == 1)
            return new boolean[]{true, false};
        return new boolean[]{false, false};
    }

    public boolean isCheckmate(boolean need, String[][] pieces, boolean isWhite) {
        // Находим позицию короля текущего игрока
        int[] kingPosition = findKingPosition(pieces, isWhite);

        // Если король не найден, то нет смысла продолжать
        if (kingPosition == null) {
            return true;
        }

        // Получаем все ходы короля
        List<int[]> kingMoves = getKingMoves(need, pieces, kingPosition[0], kingPosition[1]);
        kingMoves = moveValidate(kingMoves, isWhite);

        // Проверяем каждый ход короля
        for (int[] move : kingMoves) {
            // Если ход короля валиден, и король может уйти от угрозы, то это не мат
            if (moveValidateOnCheck(kingPosition, move) && !(isCheckByKing(need, pieces, isWhite ? "wK" : "bK", move[0], move[1])[0] == 1)) {
                return false;
            }
        }

        // Получаем все ходы союзных фигур
        List<int[]> allyMoves = getAllMoves(need, pieces, isWhite);

        // Проверяем каждый ход союзной фигуры
        for (int[] move : allyMoves) {
            // Если ход союзной фигуры может блокировать или съесть угрозу, то это не мат
            if (move[2] == 0 && move[0] == checkRow && move[1] == checkCol) {
                return false;
            }
        }

        // Если ни один ход короля или союзных фигур не предотвращает угрозу и король находится под шахом, то это мат
        return true;
    }


    private int[] findKingPosition(String[][] pieces, boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isOccupied(pieces, row, col) && isWhitePiece(pieces, row, col) == isWhite && pieces[row][col].equals((isWhite ? "wK" : "bK"))) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public List<int[]> movesValidateOnCheck(boolean need, int row, int col, List<int[]> withoutChecking) {
        if (need) {
            List<int[]> sortedList = new ArrayList<>();
            for (int[] move : withoutChecking) {
                if (moveValidateOnCheck(new int[]{row, col}, move)) {
                    sortedList.add(move);
                }
            }
            return sortedList;
        }
        return withoutChecking;
    }

    public boolean moveValidateOnCheck(int[] oldCoordinate, int[] newCoordinate) {
        dream = deepCopy(chessBoard.pieces);
        String movedPiece = dream[oldCoordinate[0]][oldCoordinate[1]];
        if (movedPiece != null) {
            dream[newCoordinate[0]][newCoordinate[1]] = movedPiece;
            dream[oldCoordinate[0]][oldCoordinate[1]] = null;

            // Check if the king is in check after the move
            int[] kingCoordinates = findKingPosition(dream, movedPiece.contains("w"));
            if (kingCoordinates != null && areOnSameLine(oldCoordinate[0], oldCoordinate[1], kingCoordinates[0], kingCoordinates[1])) {
                boolean isKingInCheck = isCheckByKing(false, dream, movedPiece.contains("w") ? "wK" : "bK", kingCoordinates[0], kingCoordinates[1])[0] == 1;
                return !isKingInCheck;
            }
            return true;
        }

        return false;
    }

    private boolean areOnSameLine(int row1, int col1, int row2, int col2) {
        return row1 == row2 || col1 == col2 || Math.abs(row1 - row2) == Math.abs(col1 - col2);
    }
    private String[][] deepCopy(String[][] original) {
        if (original == null) {
            return null;
        }

        int rows = original.length;
        int cols = original[0].length;

        String[][] copy = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, cols);
        }

        return copy;
    }
}

