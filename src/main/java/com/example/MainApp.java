package com.example;

import com.example.dao.*;
import com.example.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp {
    private static User loggedInUser;

    public static void main(String[] args) {
        // Create main frame
        JFrame frame = new JFrame("Video Sharing Platform");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);

        // Show the login form
        showLoginForm(panel, frame);

        frame.setVisible(true);
    }

    private static void showLoginForm(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(250, 200, 100, 25);
        panel.add(usernameLabel);

        JTextField usernameField = new JTextField(20);
        usernameField.setBounds(350, 200, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(250, 250, 100, 25);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(350, 250, 200, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(350, 300, 100, 25);
        panel.add(loginButton);

        // Add the Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(350, 350, 100, 25);
        panel.add(registerButton);

        registerButton.addActionListener(e -> showRegistrationForm(panel));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            loggedInUser = authenticateUser(username, password);

            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(panel, "Login successful!");
                panel.removeAll();
                displayVideos(panel);
                panel.revalidate();
                panel.repaint();
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid username or password.");
            }
        });
    }

    private static void showRegistrationForm(JPanel panel) {
        panel.removeAll();
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(250, 200, 100, 25);
        panel.add(usernameLabel);

        JTextField usernameField = new JTextField(20);
        usernameField.setBounds(350, 200, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(250, 250, 100, 25);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(350, 250, 200, 25);
        panel.add(passwordField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(250, 300, 100, 25);
        panel.add(emailLabel);

        JTextField emailField = new JTextField(20);
        emailField.setBounds(350, 300, 200, 25);
        panel.add(emailField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(350, 350, 100, 25);
        panel.add(registerButton);

        // Add Back to Login Page Button
        JButton backButton = new JButton("Back");
        backButton.setBounds(350, 400, 100, 25);
        panel.add(backButton);

        // Back button logic
        backButton.addActionListener(e -> {
            panel.removeAll(); // Clear current panel components
            showLoginForm(panel, (JFrame) SwingUtilities.getWindowAncestor(panel));
            panel.revalidate(); // Refresh the panel
            panel.repaint();   // Repaint the panel
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in all fields.");
                return;
            }

            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(panel, "Invalid email address.");
                return;
            }

            if (!email.contains(".com")) {
                JOptionPane.showMessageDialog(panel, "Invalid email address.");
                return;
            }


            boolean success = UserDAO.registerUser(username, password, email);
            if (success) {
                JOptionPane.showMessageDialog(panel, "Registration successful!");
                showLoginForm(panel, (JFrame) SwingUtilities.getWindowAncestor(panel));
            } else {
                JOptionPane.showMessageDialog(panel, "Registration failed. Username might already exist.");
            }
        });

        panel.revalidate();
        panel.repaint();
    }

    private static User authenticateUser(String username, String password) {
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }
        return null;
    }

    private static void showUploadForm(JPanel panel) {
        panel.removeAll(); // Clear the previous components
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(250, 200, 100, 25);
        panel.add(titleLabel);

        JTextField titleField = new JTextField(20);
        titleField.setBounds(350, 200, 200, 25);
        panel.add(titleField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(250, 250, 100, 25);
        panel.add(descriptionLabel);

        JTextField descriptionField = new JTextField(50);
        descriptionField.setBounds(350, 250, 200, 25);
        panel.add(descriptionField);

        JLabel fileLabel = new JLabel("Select Video:");
        fileLabel.setBounds(250, 300, 100, 25);
        panel.add(fileLabel);

        JButton fileButton = new JButton("Browse");
        fileButton.setBounds(350, 300, 100, 25);
        panel.add(fileButton);

        JLabel selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setBounds(460, 300, 300, 25);
        panel.add(selectedFileLabel);

        final String[] selectedFilePath = {null};

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFilePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                selectedFileLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        JButton uploadButton = new JButton("Upload");
        uploadButton.setBounds(350, 350, 100, 25);
        panel.add(uploadButton);

        uploadButton.addActionListener(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();

            if (selectedFilePath[0] == null) {
                JOptionPane.showMessageDialog(panel, "Please select a video file.");
                return;
            }

            if (title.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in all fields.");
                return;
            }

            VideoDAO.uploadVideo(loggedInUser.getId(), title, description, selectedFilePath[0]);
            JOptionPane.showMessageDialog(panel, "Video uploaded successfully!");
            panel.removeAll();
            displayVideos(panel); // Go back to the main screen
            panel.revalidate();
            panel.repaint();
        });

        // Add Back Button
        JButton backButton = new JButton("Back");
        backButton.setBounds(350, 400, 100, 25);
        panel.add(backButton);

        backButton.addActionListener(e -> {
            panel.removeAll();
            displayVideos(panel); // Redirect to the video display page
            panel.revalidate();
            panel.repaint();
        });

        panel.revalidate();
        panel.repaint();
    }

    private static void displayVideos(JPanel panel) {
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        // Create a top panel with Upload Video and Profile buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton uploadButton = new JButton("Upload Video");
        uploadButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(uploadButton, BorderLayout.WEST);

        JButton profileButton = new JButton("Profile");
        profileButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        topPanel.add(profileButton, BorderLayout.EAST);

        uploadButton.addActionListener(e -> showUploadForm(panel));
        profileButton.addActionListener(e -> showProfileManagement(panel));

        // Create a scrollable panel for videos
        JPanel videoContainer = new JPanel();
        videoContainer.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(videoContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Populate videos
        List<Video> videos = VideoDAO.fetchVideos();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between video cards
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (Video video : videos) {
            JPanel videoCard = new JPanel();
            videoCard.setLayout(new BoxLayout(videoCard, BoxLayout.Y_AXIS));
            videoCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            videoCard.setBackground(Color.WHITE);

            JLabel titleLabel = new JLabel(video.getTitle());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel descriptionLabel = new JLabel("<html><p style='width:200px;'>" + video.getDescription() + "</p></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton playButton = new JButton("Play");
            playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            playButton.addActionListener(e -> VideoPlayer.playVideo(video.getVideoUrl()));

            // Like and Dislike Buttons
            JPanel likeDislikePanel = new JPanel();
            JButton likeButton = new JButton("Like (" + video.getLikes() + ")");
            JButton dislikeButton = new JButton("Dislike (" + video.getDislikes() + ")");
            likeDislikePanel.add(likeButton);
            likeDislikePanel.add(dislikeButton);

            likeButton.addActionListener(e -> {
                VideoDAO.likeVideo(video.getId());
                JOptionPane.showMessageDialog(panel, "You liked this video!");
                displayVideos(panel); // Refresh the display
            });

            dislikeButton.addActionListener(e -> {
                VideoDAO.dislikeVideo(video.getId());
                JOptionPane.showMessageDialog(panel, "You disliked this video!");
                displayVideos(panel); // Refresh the display
            });

            // Add Comments Section
            JLabel commentsLabel = new JLabel("Comments:");
            commentsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            commentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel commentsPanel = new JPanel();
            commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));

            // Fetch and display comments for the video
            List<Comment> comments = CommentDAO.fetchComments(video.getId());
            if (comments.isEmpty()) {
                JLabel noCommentsLabel = new JLabel("No comments yet.");
                noCommentsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                commentsPanel.add(noCommentsLabel);
            } else {
                for (Comment comment : comments) {
                    JLabel commentLabel = new JLabel("- " + comment.getComment());
                    commentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    commentsPanel.add(commentLabel);
                }
            }

            // Add a text field to post a new comment
            JTextField commentField = new JTextField(30);
            JButton commentButton = new JButton("Post Comment");
            commentButton.addActionListener(e -> {
                String newComment = commentField.getText();
                if (!newComment.isEmpty()) {
                    CommentDAO.addComment(video.getId(), loggedInUser.getId(), newComment);
                    JOptionPane.showMessageDialog(panel, "Comment added!");
                    panel.removeAll();
                    displayVideos(panel); // Refresh the display
                    panel.revalidate();
                    panel.repaint();
                }
            });

            JPanel commentInputPanel = new JPanel();
            commentInputPanel.add(commentField);
            commentInputPanel.add(commentButton);

            // Assemble Video Card
            videoCard.add(titleLabel);
            videoCard.add(Box.createRigidArea(new Dimension(0, 5)));
            videoCard.add(descriptionLabel);
            videoCard.add(Box.createRigidArea(new Dimension(0, 5)));
            videoCard.add(playButton);
            videoCard.add(Box.createRigidArea(new Dimension(0, 10)));
            videoCard.add(likeDislikePanel); // Add Like/Dislike buttons
            videoCard.add(Box.createRigidArea(new Dimension(0, 10)));
            videoCard.add(commentsLabel);
            videoCard.add(commentsPanel);
            videoCard.add(Box.createRigidArea(new Dimension(0, 5)));
            videoCard.add(commentInputPanel);

            videoContainer.add(videoCard, gbc);
            gbc.gridy++;
        }

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.revalidate();
        panel.repaint();
    }

    private static void showProfileManagement(JPanel panel) {
        panel.removeAll();
        panel.setLayout(null);

        JLabel headerLabel = new JLabel("Manage Your Profile");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBounds(250, 50, 300, 30);
        panel.add(headerLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(200, 150, 100, 25);
        panel.add(usernameLabel);

        JTextField usernameField = new JTextField(loggedInUser.getUsername());
        usernameField.setBounds(300, 150, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(200, 200, 100, 25);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(300, 200, 200, 25);
        panel.add(passwordField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(250, 250, 150, 25);
        panel.add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(450, 250, 150, 25);
        panel.add(backButton);

        // Save Changes Action
        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required.");
                return;
            }

            loggedInUser.setUsername(newUsername);
            loggedInUser.setPassword(newPassword);

            UserDAO.updateUser(loggedInUser);
            JOptionPane.showMessageDialog(panel, "Profile updated successfully!");
        });

        // Back to Display Videos Action
        backButton.addActionListener(e -> {
            panel.removeAll();
            displayVideos(panel);
            panel.revalidate();
            panel.repaint();
        });

        panel.revalidate();
        panel.repaint();
    }

}