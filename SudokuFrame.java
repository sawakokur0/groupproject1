import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import database.SudokuRepository;
import entities.Board;
import entities.User;
import entities.Solver;

public class SudokuFrame extends JFrame {
    private static final int SIZE = 9;
    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final SudokuRepository repository = SudokuRepository.getInstance();
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

        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            getUserInput();
            return;
        }

        User.Role role = selectUserRole();
        Optional<User> savedUser = repository.saveUser(new User(name, role));

        savedUser.ifPresentOrElse(user -> {
            System.out.println("User created successfully: " + user);
            currentUser = user;
            loadGameBoard();
        }, () -> {
            System.out.println("ERROR: Failed to create user.");
            JOptionPane.showMessageDialog(this, "Error creating user. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        });

    }

    private User.Role selectUserRole() {
        String[] roles = {"STUDENT", "TEACHER", "PLAYER", "OTHER"};
        String selectedRole = (String) JOptionPane.showInputDialog(this, "Select role:", "Role Selection",
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
        return User.Role.valueOf(selectedRole);
    }

    private void loadGameBoard() {
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

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton checkButton = new JButton("Check Solution");
        JButton saveButton = new JButton("Save Progress");
        JButton solveButton = new JButton("Solve");

        checkButton.addActionListener(this::checkSolution);
        saveButton.addActionListener(e -> saveProgress());
        solveButton.addActionListener(e -> solveBoard());

        buttonPanel.add(checkButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(solveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void checkSolution(ActionEvent e) {
        int[][] boardData = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                boardData[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }

        Board board = new Board(boardData);
        boolean isSolved = board.isComplete() && board.isValid();

        if (isSolved) {
            JOptionPane.showMessageDialog(null, "Congratulations! You solved the Sudoku!");
        }
        else {
            Solver solver = new Solver(board);
            solver.solve();
            showCorrectSolution(board.getBoard());
            JOptionPane.showMessageDialog(null, "Incorrect solution. Here's the correct one.");
        }

        repository.saveProgress(currentUser.getId(), board, isSolved);
    }

    private void solveBoard() {
        if (!isAuthorized(User.Role.STUDENT)) return;

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
            repository.saveProgress(currentUser.getId(), board, true);
            JOptionPane.showMessageDialog(null, "Solution saved to database!");
        }
        else {
            JOptionPane.showMessageDialog(null, "No solution exists.");
        }
    }

    private void saveProgress() {
        if (!isAuthorized(User.Role.TEACHER)) return;

        int[][] boardData = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                boardData[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        Board board = new Board(boardData);
        repository.saveProgress(currentUser.getId(), board, false);
        JOptionPane.showMessageDialog(null, "Progress saved.");
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
    private void showCorrectSolution(int[][] correctBoard) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText(String.valueOf(correctBoard[row][col]));
                cells[row][col].setEditable(false);
                cells[row][col].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    private boolean isAuthorized(User.Role requiredRole) {
        if (currentUser.getRole().ordinal() < requiredRole.ordinal()) {
            JOptionPane.showMessageDialog(null, "Access Denied! You need " + requiredRole + " privileges.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
