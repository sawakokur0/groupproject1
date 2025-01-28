package database;

import entities.Board;
import entities.User;

import java.sql.*;

public class SudokuRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/sudoku";
    private static final String USER = "postgres";
    private static final String PASSWORD = "0000";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public User saveUser(User user) {
        String sql = "INSERT INTO users(name) VALUES(?) RETURNING id";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error saving the user: " + e.getMessage());
        }
        return user;
    }

    public void saveProgress(int userId, Board board, boolean isSolved) {
        String sql = "INSERT INTO games(user_id, board, is_solved) VALUES(?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, board.toString());
            pstmt.setBoolean(3, isSolved);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving the game progress: " + e.getMessage());
        }
    }

    public Board getBoard(int id) {
        String sql = "SELECT board FROM games WHERE id = ?";
        Board board = null;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                board = parseBoard(rs.getString("board"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving the board: " + e.getMessage());
        }
        return board;
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

    public void saveBoard(Board board, Board solution) {
        String sql = "INSERT INTO Sudoku(board, solution) VALUES(?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, board.toString());
            pstmt.setString(2, solution.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving the board: " + e.getMessage());
        }
    }
}
