import java.util.Scanner;

public class Main {
    private static final int GRID_SIZE = 9;

    public static void main(String[] args) {
        int[][] board = {
                {0, 2, 4, 0, 0, 0, 0, 9, 3},
                {0, 1, 0, 3, 0, 2, 0, 6, 0},
                {3, 8, 6, 1, 9, 0, 5, 2, 4},
                {0, 6, 7, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 4, 0, 1, 2},
                {0, 3, 1, 0, 0, 5, 7, 0, 0},
                {1, 0, 0, 9, 2, 0, 4, 3, 0},
                {0, 0, 3, 0, 5, 0, 2, 0, 6},
                {0, 5, 0, 0, 7, 0, 0, 8, 1}
        };

        playGame(board);
    }

    private static void playGame(int[][] board) {
        Scanner scanner = new Scanner(System.in);

        while (!isBoardFull(board)) {
            printBoard(board);
            System.out.println("Enter line (1-9), column (1-9) and number (1-9) separated by space (for example, 3 5 7):");

            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;
            int num = scanner.nextInt();

            if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE || num < 1 || num > 9) {
                System.out.println("Error: Enter valid values from 1 to 9.");
                continue;
            }

            if (board[row][col] != 0) {
                System.out.println("Error: This cell is already full!");
                continue;
            }

            if (isValidPlace(board, num, row, col)) {
                board[row][col] = num;
            } else {
                System.out.println("Error: The number " + num + " cannot be placed in this cell!");
            }
        }

        System.out.println("Congratulations! You've solved Sudoku.");
        printBoard(board);
        scanner.close();
    }

    private static boolean isBoardFull(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void printBoard(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.println("------+-------+------");
            }
            for (int col = 0; col < GRID_SIZE; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("| ");
                }
                System.out.print((board[row][col] == 0 ? "." : board[row][col]) + " ");
            }
            System.out.println();
        }
    }

    private static boolean isNumberInRow(int[][] board, int row, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInCol(int[][] board, int col, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] == num) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNumberInBox(int[][] board, int row, int col, int num) {
        int localBoxRow = row - row % 3;
        int localBoxCol = col - col % 3;

        for (int i = localBoxRow; i < localBoxRow + 3; i++) {
            for (int j = localBoxCol; j < localBoxCol + 3; j++) {
                if (board[i][j] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValidPlace(int[][] board, int num, int row, int col) {
        return !isNumberInRow(board, row, num) && !isNumberInCol(board, col, num) && !isNumberInBox(board, row, col, num);
    }
}