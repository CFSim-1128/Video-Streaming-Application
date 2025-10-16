package com.example.models;

public class Video {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String videoUrl;
    private int likes;
    private int dislikes;

    // Constructor
    public Video(int id, int userId, String title, String description, String videoUrl, int likes, int dislikes) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    // Setters (if needed)
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
