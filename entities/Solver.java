package entities;

public class Solver {
    private final Board board;

    public Solver(Board board) {
        this.board = board;
    }

    public boolean solve() {
        return solveBoard(board.getBoard());
    }

    private boolean solveBoard(int[][] sudoku) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudoku[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValidMove(sudoku, row, col, num)) {
                            sudoku[row][col] = num;
                            if (solveBoard(sudoku)) {
                                return true;
                            }
                            sudoku[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidMove(int[][] board, int row, int col, int num) {
        return isRowValid(board, row, num) &&
                isColumnValid(board, col, num) &&
                isBoxValid(board, row, col, num);
    }

    private boolean isRowValid(int[][] board, int row, int num) {
        return java.util.stream.IntStream.range(0, 9).noneMatch(col -> board[row][col] == num);
    }

    private boolean isColumnValid(int[][] board, int col, int num) {
        return java.util.stream.IntStream.range(0, 9).noneMatch(row -> board[row][col] == num);
    }

    private boolean isBoxValid(int[][] board, int row, int col, int num) {
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;

        return java.util.stream.IntStream.range(0, 3).noneMatch(i ->
                java.util.stream.IntStream.range(0, 3).anyMatch(j -> board[boxRow + i][boxCol + j] == num)
        );
    }
}
