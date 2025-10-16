package com.example.models;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int videoId;
    private int userId;
    private String comment;
    private Timestamp createdAt;

    // Constructor
    public Comment(int id, int videoId, int userId, String comment, Timestamp createdAt) {
        this.id = id;
        this.videoId = videoId;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public int getVideoId() { return videoId; }
    public int getUserId() { return userId; }
    public String getComment() { return comment; }
    public Timestamp getCreatedAt() { return createdAt; }
}
