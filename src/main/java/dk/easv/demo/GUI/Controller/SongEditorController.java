package dk.easv.demo.GUI.Controller;

import dk.easv.demo.BE.Song;
import dk.easv.demo.BLL.SongManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SongEditorController implements Initializable {

    @FXML private TextField titleField;
    @FXML private TextField artistField;
    @FXML private TextField genreField;
    @FXML private TextField durationField;
    @FXML private TextField filePathField;
    @FXML private Button saveButton, cancelButton, browseButton;

    private SongManager songManager;
    private Song currentSong;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songManager = new SongManager();
    }

    public void setSong(Song song) {
        this.currentSong = song;
        if (song != null) {
            titleField.setText(song.getTitle());
            artistField.setText(song.getArtist());
            genreField.setText(song.getCategory());
            durationField.setText(String.valueOf(song.getDuration()));
            filePathField.setText(song.getFilePath());
        }
    }

    @FXML
    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Music File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac"),
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                new FileChooser.ExtensionFilter("WAV Files", "*.wav"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Set initial directory to music folder if it exists
        File musicDir = new File("music");
        if (musicDir.exists() && musicDir.isDirectory()) {
            fileChooser.setInitialDirectory(musicDir);
        } else {
            File myTunesMusic = new File("MyTunesMusic");
            if (myTunesMusic.exists() && myTunesMusic.isDirectory()) {
                fileChooser.setInitialDirectory(myTunesMusic);
            }
        }

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            // Store relative path if file is in music folder
            String filePath = selectedFile.getAbsolutePath();

            // Try to make path relative to music folder for portability
            File musicFolder = new File("music");
            if (selectedFile.getAbsolutePath().contains(musicFolder.getAbsolutePath())) {
                // File is in or under music folder, use relative path
                File currentDir = new File(System.getProperty("user.dir"));
                String relativePath = currentDir.toURI().relativize(selectedFile.toURI()).getPath();
                filePathField.setText(relativePath);
            } else {
                filePathField.setText(filePath);
            }

            // Auto-fill title from filename if empty
            if (titleField.getText().isEmpty()) {
                String fileName = selectedFile.getName();
                // Remove file extension
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileName = fileName.substring(0, dotIndex);
                }
                titleField.setText(fileName);
            }
        }
    }

    @FXML
    private void saveSong() {
        String title = titleField.getText().trim();
        String artist = artistField.getText().trim();
        String genre = genreField.getText().trim();
        String durationText = durationField.getText().trim();
        String filePath = filePathField.getText().trim();

        // Validation
        if (title.isEmpty() || artist.isEmpty() || durationText.isEmpty() || filePath.isEmpty()) {
            showErrorDialog("Please fill in all required fields");
            return;
        }

        // Parse duration from String to int
        int duration;
        try {
            duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                showErrorDialog("Duration must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Duration must be a number (seconds)");
            return;
        }

        // Check if file exists (try multiple locations)
        if (!checkFileExists(filePath)) {
            showErrorDialog("The selected file does not exist at: " + filePath +
                    "\n\nPlease make sure the file is in one of these locations:\n" +
                    "- 'music' folder next to the application\n" +
                    "- Your home directory/MyTunesMusic/\n" +
                    "- Same folder as the application");
            return;
        }

        try {
            if (currentSong == null) {
                // FIXED: Use individual parameters instead of Song object
                Song createdSong = songManager.createSong(title, artist, genre, duration, filePath);
                showInfoDialog("Success", "Song '" + title + "' created successfully");
            } else {
                // Update existing song
                currentSong.setTitle(title);
                currentSong.setArtist(artist);
                currentSong.setCategory(genre);
                currentSong.setDuration(duration);
                currentSong.setFilePath(filePath);
                songManager.updateSong(currentSong);
                showInfoDialog("Success", "Song '" + title + "' updated successfully");
            }

            closeWindow();

        } catch (SQLException e) {
            showErrorDialog("Error saving song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkFileExists(String filePath) {
        // Try different possible locations (same logic as MainController)
        String fileName = new File(filePath).getName();

        String[] possibleLocations = {
                filePath,
                "music/" + fileName,
                "../music/" + fileName,
                "./music/" + fileName,
                System.getProperty("user.home") + "/MyTunesMusic/" + fileName,
                System.getProperty("user.home") + "/Music/" + fileName,
                System.getProperty("user.dir") + "/music/" + fileName,
                System.getProperty("user.dir") + "/MyTunesMusic/" + fileName,
                fileName
        };

        for (String location : possibleLocations) {
            File file = new File(location);
            if (file.exists() && file.isFile()) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}