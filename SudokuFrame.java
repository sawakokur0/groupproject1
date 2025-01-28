import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import database.SudokuRepository;
import entities.Board;
import entities.User;
import entities.Solver;

public class SudokuFrame extends JFrame {
    private static final int SIZE = 9;
    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final SudokuRepository repository = new SudokuRepository();
    private User currentUser;
    private final int[][] initialBoard = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };


    public SudokuFrame() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getUserInput();
    }

    private void getUserInput() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:", "Welcome", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            currentUser = repository.saveUser(new User(name));
            loadGameBoard();
        } else {
            JOptionPane.showMessageDialog(this, "Name cannot be empty. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            getUserInput();
        }
    }

    private void loadGameBoard() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                if (initialBoard[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(initialBoard[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(Color.LIGHT_GRAY);
                }
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                gridPanel.add(cells[row][col]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JButton checkButton = new JButton("Check Solution");
        checkButton.addActionListener(new CheckSolutionListener());
        add(checkButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void solveBoard() {
        int[][] boardData = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                boardData[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }

        Board board = new Board(boardData);
        Solver solver = new Solver(board);
        if (solver.solve()) {
            displaySolution(board);
            repository.saveBoard(board, board);
            JOptionPane.showMessageDialog(null, "Solution saved to database!");
        } else {
            JOptionPane.showMessageDialog(null, "No solution exists.");
        }
    }

    private void displaySolution(Board board) {
        int[][] solution = board.getBoard();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText(String.valueOf(solution[row][col]));
                cells[row][col].setEditable(false);
            }
        }
    }


    private JTextField createNumberTextField() {
        JTextField textField = new JTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setDocument(new NumberDocument());
        return textField;
    }

    private class CheckSolutionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[][] board = new int[SIZE][SIZE];

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    String text = cells[row][col].getText();
                    if (text.isEmpty()) {
                        board[row][col] = 0;
                    } else {
                        board[row][col] = Integer.parseInt(text);
                    }
                }
            }

            Board gameBoard = new Board(board);
            boolean isSolved = gameBoard.isComplete() && gameBoard.isValid();

            if (isSolved) {
                JOptionPane.showMessageDialog(null, "Congratulations! You solved the Sudoku!");
            } else {
                Solver solver = new Solver(gameBoard);
                solver.solve();
                showCorrectSolution(gameBoard.getBoard());
                JOptionPane.showMessageDialog(null, "Your solution is incorrect. Here is the correct solution.");
            }

            repository.saveProgress(currentUser.getId(), gameBoard, isSolved);
        }
    }

    private void showCorrectSolution(int[][] correctBoard) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText(String.valueOf(correctBoard[row][col]));
                cells[row][col].setEditable(false);
                cells[row][col].setBackground(Color.LIGHT_GRAY);
            }
        }
    }
}
