package com.example.dao;

import com.example.DatabaseConnection;
import com.example.models.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // Fetch all comments for a specific video
    public static List<Comment> fetchComments(int videoId) {
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM comments WHERE video_id = ?")) {

            stmt.setInt(1, videoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("video_id"),
                        rs.getInt("user_id"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
        }
        return comments;
    }

    // Add a new comment
    public static void addComment(int videoId, int userId, String comment) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO comments (video_id, user_id, comment) VALUES (?, ?, ?)")) {

            stmt.setInt(1, videoId);
            stmt.setInt(2, userId);
            stmt.setString(3, comment);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
        }
    }
}
