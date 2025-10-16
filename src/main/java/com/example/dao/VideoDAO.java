package com.example.dao;

import com.example.DatabaseConnection;
import com.example.models.Video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO {

    // Fetch all videos
    public static List<Video> fetchVideos() {
        List<Video> videos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM videos")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("video_url"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching videos: " + e.getMessage());
        }
        return videos;
    }

    // Search videos by title or description
    public static List<Video> searchVideos(String query) {
        List<Video> videos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM videos WHERE title LIKE ? OR description LIKE ?")) {

            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("video_url"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error searching videos: " + e.getMessage());
        }
        return videos;
    }

    // Upload a new video
    public static void uploadVideo(int userId, String title, String description, String videoUrl) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO videos (user_id, title, description, video_url) VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, videoUrl);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error uploading video: " + e.getMessage());
        }
    }

    // Increment likes for a video
    public static void likeVideo(int videoId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE videos SET likes = likes + 1 WHERE id = ?")) {

            stmt.setInt(1, videoId);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error liking video: " + e.getMessage());
        }
    }

    // Increment dislikes for a video
    public static void dislikeVideo(int videoId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE videos SET dislikes = dislikes + 1 WHERE id = ?")) {

            stmt.setInt(1, videoId);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error disliking video: " + e.getMessage());
        }
    }

    public static List<Video> advancedSearch(String query, String uploader, String startDate, String endDate, int minLikes) {
        List<Video> videos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT v.*, u.username FROM videos v LEFT JOIN users u ON v.user_id = u.id WHERE 1=1");

        if (!query.isEmpty()) {
            sql.append(" AND (v.title LIKE ? OR v.description LIKE ?)");
        }
        if (!uploader.isEmpty()) {
            sql.append(" AND u.username LIKE ?");
        }
        if (!startDate.isEmpty()) {
            sql.append(" AND v.created_at >= ?");
        }
        if (!endDate.isEmpty()) {
            sql.append(" AND v.created_at <= ?");
        }
        if (minLikes > 0) {
            sql.append(" AND v.likes >= ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (!query.isEmpty()) {
                stmt.setString(index++, "%" + query + "%");
                stmt.setString(index++, "%" + query + "%");
            }
            if (!uploader.isEmpty()) {
                stmt.setString(index++, "%" + uploader + "%");
            }
            if (!startDate.isEmpty()) {
                stmt.setString(index++, startDate);
            }
            if (!endDate.isEmpty()) {
                stmt.setString(index++, endDate);
            }
            if (minLikes > 0) {
                stmt.setInt(index++, minLikes);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("video_url"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error performing advanced search: " + e.getMessage());
        }

        return videos;
    }

}
