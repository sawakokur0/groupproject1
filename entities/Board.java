package entities;

import java.util.Arrays;

public class Board {
    private static final int SIZE = 9;
    private final int[][] board;

    public Board(int[][] board) {
        if (board == null || board.length != SIZE || Arrays.stream(board).anyMatch(row -> row.length != SIZE)) {
            throw new IllegalArgumentException("Invalid board size. Must be 9x9.");
        }
        this.board = deepCopy(board);
    }

    public boolean isValidMove(int row, int col, int num) {
        if (board[row][col] != 0) return false;
        return isRowValid(row, num) && isColumnValid(col, num) && isBoxValid(row, col, num);
    }

    public boolean isComplete() {
        return Arrays.stream(board).flatMapToInt(Arrays::stream).noneMatch(cell -> cell == 0);
    }

    public boolean isValid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int num = board[row][col];
                if (num != 0) {
                    board[row][col] = 0;
                    if (!isValidMove(row, col, num)) {
                        board[row][col] = num;
                        return false;
                    }
                    board[row][col] = num;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return deepCopy(board);
    }

    private boolean isRowValid(int row, int num) {
        return Arrays.stream(board[row]).noneMatch(cell -> cell == num);
    }

    private boolean isColumnValid(int col, int num) {
        return Arrays.stream(board).mapToInt(row -> row[col]).noneMatch(cell -> cell == num);
    }

    private boolean isBoxValid(int row, int col, int num) {
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;

        return Arrays.stream(board, boxRow, boxRow + 3)
                .flatMapToInt(r -> Arrays.stream(r, boxCol, boxCol + 3)) .noneMatch(cell -> cell == num);
    }

    private int[][] deepCopy(int[][] original) {
        return Arrays.stream(original).map(int[]::clone).toArray(int[][]::new);
    }

    @Override
    public String toString() {
        return Arrays.stream(board).map(row -> Arrays.stream(row).mapToObj(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .reduce((a, b) -> a + "\n" + b).orElse("");
    }
}
