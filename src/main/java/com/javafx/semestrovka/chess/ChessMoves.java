package com.javafx.semestrovka.chess;

import com.javafx.semestrovka.ChessBoard;

import java.util.ArrayList;
import java.util.List;

import static com.javafx.semestrovka.ChessBoard.pieces;

public class ChessMoves {
    public static List<int[]> getUniversalMoves(String type, int row, int col) {
        if ("bP".equals(type)) {
            return getPawnMoves(row, col, false);
        } else if ("wP".equals(type)) {
            return getPawnMoves(row, col, true);
        } else if (type.contains("N")) {
            return getKnightMoves(row, col);
        } else if (type.contains("B")) {
            return getBishopMoves(row, col);
        } else if (type.contains("R")) {
            return getRookMoves(row, col);
        } else if (type.contains("Q")) {
            return getQueenMoves(row, col);
        } else if (type.contains("K")) {
            return getKingMoves(row, col);
        }

        throw new IllegalArgumentException("Unknown piece type: " + type);
    }

    public static List<int[]> getPawnMoves(int row, int col, boolean isWhite) {
        List<int[]> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;

        // Вперед
        int newRow = row + direction;
        if (isValidSquare(newRow, col) && !isOccupied(newRow, col)) {
            moves.add(new int[]{newRow, col});
        }

        // Вперед на две клетки (для начального хода)
        int initialRow = isWhite ? 6 : 1;
        if (row == initialRow) {
            int doubleMoveRow = row + 2 * direction;
            if (isValidSquare(doubleMoveRow, col) && !isOccupied(doubleMoveRow, col)) {
                moves.add(new int[]{doubleMoveRow, col});
            }
        }

        // Диагональные атаки
        int[] attackCols = {col - 1, col + 1};
        for (int attackCol : attackCols) {
            if (isValidSquare(newRow, attackCol) && isOccupied(newRow, attackCol) && isOpponentPiece(row, col, newRow, attackCol)) {
                moves.add(new int[]{newRow, attackCol});
            }
        }

        return moves;
    }

    private static boolean isOpponentPiece(int row, int col, int targetRow, int targetCol) {
        // Проверка, является ли фигура на заданных координатах противниковой
        boolean isWhite = ChessBoard.isWhitePiece(row, col);
        return isWhite != ChessBoard.isWhitePiece(targetRow, targetCol);
    }


    public static List<int[]> getKnightMoves(int row, int col) {
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

        return moves;
    }

    public static List<int[]> getRookMoves(int row, int col) {
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
                if (isOccupied(newRow, newCol)) {
                    break; // Stop if there is a piece in the way
                }
                newRow += move[0];
                newCol += move[1];
            }
        }

        return moves;
    }

    public static List<int[]> getBishopMoves(int row, int col) {
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
                if (isOccupied(newRow, newCol)) {
                    break; // Stop if there is a piece in the way
                }
                newRow += move[0];
                newCol += move[1];
            }
        }

        return moves;
    }


    private static boolean isOccupied(int row, int col) {
        // Проверка, занята ли клетка фигурой
        return ChessBoard.isOccupied(row, col);
    }


    public static List<int[]> getQueenMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Комбинация ходов слона и ладьи
        moves.addAll(getBishopMoves(row, col));
        moves.addAll(getRookMoves(row, col));

        return moves;
    }

    public static boolean isCheck(String type, int row, int col) {
        List<int[]> moves = getUniversalMoves(type, row, col);
        for (int[] move: moves) {
            if (isOccupied(move[0], move[1])) {
                if (pieces[move[0]][move[1]].contains("K") && type.charAt(0) != pieces[move[0]][move[1]].charAt(0)) {
                    System.out.println("Check for: " + pieces[move[0]][move[1]]);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isCheckForKing(String type, int row, int col) {
        // Find the opponent's moves
        boolean isWhite = ChessBoard.isWhitePiece(row, col);
        List<int[]> opponentMoves = getAllMoves(!isWhite);

        // Check if any opponent's move targets the king's position
        for (int[] move : opponentMoves) {
            if (move[0] == row && move[1] == col) {
                System.out.println(type + " is in check!");
                return true;
            }
        }

        return false;
    }


    public static List<int[]> getKingMoves(int row, int col) {
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
        if (ChessBoard.canCastleKingside(row, col)) {
            moves.add(new int[]{row, col + 2});
        }

        if (ChessBoard.canCastleQueenside(row, col)) {
            moves.add(new int[]{row, col - 2});
        }


        return moves;
    }


    // Другие функции для других фигур могут быть добавлены аналогичным образом

    private static boolean isValidSquare(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static boolean isCheck(int kingRow, int kingCol, boolean isWhite) {
        List<int[]> opponentMoves = getAllMoves(!isWhite);
        for (int[] move : opponentMoves) {
            if (move[0] == kingRow && move[1] == kingCol) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCheckmate(int kingRow, int kingCol, boolean isWhite) {
        // Check if the king is in check
        if (!isCheck(kingRow, kingCol, isWhite)) {
            return false;
        }

        // Check if the king can escape to any square
        List<int[]> kingMoves = getKingMoves(kingRow, kingCol);
        for (int[] move : kingMoves) {
            if (!isCheck(move[0], move[1], isWhite)) {
                return false;
            }
        }

        // Check if any piece can block or capture the attacking piece
        List<int[]> allMoves = getAllMoves(isWhite);
        for (int[] move : allMoves) {
            List<int[]> moves = getUniversalMoves(pieces[move[0]][move[1]], move[0], move[1]);
            moves.removeIf(m -> m[0] == kingRow && m[1] == kingCol);
            for (int[] blockingMove : moves) {
                if (!isCheck(blockingMove[0], blockingMove[1], isWhite)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<int[]> getAllMoves(boolean isWhite) {
        List<int[]> allMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isOccupied(row, col) && ChessBoard.isWhitePiece(row, col) == isWhite) {
                    String pieceType = pieces[row][col];
                    allMoves.addAll(getUniversalMoves(pieceType, row, col));
                }
            }
        }
        return allMoves;
    }


    public static void main(String[] args) {
        // Пример использования
        List<int[]> pawnMoves = getPawnMoves(1, 2, true);
        System.out.println("Pawn moves: " + pawnMoves);

        List<int[]> knightMoves = getKnightMoves(3, 3);
        System.out.println("Knight moves: " + knightMoves);
    }
}

