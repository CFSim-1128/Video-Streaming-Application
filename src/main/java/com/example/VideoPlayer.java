package com.example;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayer {
    private static MediaPlayer mediaPlayer;
    private static Stage videoStage;

    static {
        Platform.startup(() -> {}); // Initialize JavaFX runtime
    }

    public static void playVideo(String videoPath) {
        Platform.runLater(() -> {
            try {
                // Close the previous Stage if it exists
                if (videoStage != null) {
                    videoStage.close();
                    videoStage = null;
                }

                // Dispose of the previous MediaPlayer if it exists
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                }

                // Validate the video file
                File videoFile = new File(videoPath);
                if (!videoFile.exists()) {
                    System.err.println("Video file not found: " + videoPath);
                    return;
                }

                // Create a new Stage
                videoStage = new Stage();
                videoStage.setTitle("Video Player");

                // Create new Media and MediaPlayer
                Media media = new Media(videoFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                // Add the play, pause, and stop buttons
                Button playButton = new Button("Play");
                Button pauseButton = new Button("Pause");
                Button stopButton = new Button("Stop");

                playButton.setOnAction(e -> mediaPlayer.play());
                pauseButton.setOnAction(e -> mediaPlayer.pause());
                stopButton.setOnAction(e -> mediaPlayer.stop());

                // Time Slider
                Slider timeSlider = new Slider();
                timeSlider.setMin(0);
                timeSlider.setMax(1);
                timeSlider.setValue(0);

                // Add listeners to update the slider and seek video
                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                        if (!timeSlider.isValueChanging() && mediaPlayer.getTotalDuration() != null) {
                            timeSlider.setValue(newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis());
                        }
                    });

                    timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if (timeSlider.isValueChanging() && mediaPlayer.getTotalDuration() != null) {
                            mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(newValue.doubleValue()));
                        }
                    });

                    // Automatically play the video when ready
                    mediaPlayer.play();
                });

                // Controls Layout
                HBox controls = new HBox(10, playButton, pauseButton, stopButton, timeSlider);
                controls.setStyle("-fx-padding: 10; -fx-alignment: center;");

                // Root Layout
                BorderPane root = new BorderPane();
                root.setCenter(mediaView);
                root.setBottom(controls);

                Scene scene = new Scene(root, 800, 600);
                videoStage.setScene(scene);

                // Show the Stage
                videoStage.show();

                // Handle Stage close to clean up resources
                videoStage.setOnCloseRequest(event -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        mediaPlayer = null;
                    }
                    videoStage = null; // Reset the stage reference
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
