package com.example.dao;

import com.example.DatabaseConnection;
import com.example.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // Required for ResultSet
import java.sql.SQLException;

public class UserDAO {

    // Register a new user
    public static boolean registerUser(String username, String password, String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Update user profile
    public static void updateUser(User user) {
        String query = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Correctly updates the password column
            stmt.setInt(3, user.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

}
