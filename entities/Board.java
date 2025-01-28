package entities;

public class Board {
    private static final int SIZE = 9;
    private int[][] board;

    public Board(int[][] board) {
        this.board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }

    public boolean isValidMove(int row, int col, int num) {
        if (board[row][col] != 0) return false;


        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) return false;
        }

        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num) return false;
        }


        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[startRow + i][startCol + j] == num) return false;
            }
        }

        return true;
    }


    public boolean isComplete() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
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
        return board;
    }
}
