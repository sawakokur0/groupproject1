package database;

import entities.Board;
import entities.User;
import java.sql.*;
import java.util.Optional;

public class SudokuRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/sudokugame";
    private static final String USER = "postgres";
    private static final String PASSWORD = "0000";

    private static SudokuRepository instance;
    private final Connection connection;

    public SudokuRepository() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }


    public static synchronized SudokuRepository getInstance() {
        if (instance == null) {
            instance = new SudokuRepository();
        }
        return instance;
    }

    public Optional<User> saveUser(User user) {
        String sql = "INSERT INTO users(name, role) VALUES(?, ?) RETURNING id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getRole().name());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                return Optional.of(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void saveProgress(int userId, Board board, boolean isSolved) {
        if (board == null) { throw new IllegalArgumentException("Board cannot be null");
        }

        String sql = "INSERT INTO games(user_id, board, is_solved) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, board.toString());
            pstmt.setBoolean(3, isSolved);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error saving game progress: " + e.getMessage());
        }
    }

    public Optional<Board> getBoard(int id) {
        String sql = "SELECT board FROM games WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseBoard(rs.getString("board")));
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving board: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<String> getFullGameDescription(int gameId) {
        String sql = "SELECT g.id, g.board, g.is_solved, u.name, u.role " +  "FROM games g JOIN users u ON g.user_id = u.id WHERE g.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(String.format("Game ID: %d, Player: %s (%s), Solved: %s, Board:\n%s",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getBoolean("is_solved") ? "Yes" : "No",
                        rs.getString("board")));
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving game details: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Board parseBoard(String boardStr) {
        int[][] board = new int[9][9];
        String[] rows = boardStr.split("\n");

        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(",");
            for (int j = 0; j < cols.length; j++) {
                board[i][j] = Integer.parseInt(cols[j]);
            }
        }
        return new Board(board);
    }
}
