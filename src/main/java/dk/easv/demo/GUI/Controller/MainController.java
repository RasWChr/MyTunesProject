package dk.easv.demo.GUI.Controller;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;
import dk.easv.demo.BLL.SongManager;
import dk.easv.demo.BLL.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<Song> songsTableView;
    @FXML private TableView<Playlist> playlistsTableView;
    @FXML private ListView<Song> playlistSongsListView;
    @FXML private TextField filterField;
    @FXML private Button filterButton;
    @FXML private Label nowPlayingLabel, currentTimeLabel, totalTimeLabel;
    @FXML private Button playButton, pauseButton, stopButton;
    @FXML private Slider volumeSlider;
    @FXML private ProgressBar progressBar;

    private ObservableList<Song> allSongs;
    private ObservableList<Playlist> allPlaylists;
    private ObservableList<Song> currentPlaylistSongs;
    private FilteredList<Song> filteredSongs;

    private MediaPlayer mediaPlayer;
    private SongManager songManager;
    private PlaylistManager playlistManager;
    private Playlist selectedPlaylist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            songManager = new SongManager();
            playlistManager = new PlaylistManager();

            allSongs = FXCollections.observableArrayList();
            allPlaylists = FXCollections.observableArrayList();
            currentPlaylistSongs = FXCollections.observableArrayList();

            setupMediaPlayer();
            setupTableViews();
            setupEventHandlers();
            loadDataFromDatabase();

            // Set initial button states
            pauseButton.setDisable(true);

        } catch (Exception e) {
            showErrorDialog("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupMediaPlayer() {
        volumeSlider.setValue(50);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });
    }

    private void setupTableViews() {
        // Setup songs table with filtering
        filteredSongs = new FilteredList<>(allSongs, p -> true);
        SortedList<Song> sortedSongs = new SortedList<>(filteredSongs);
        sortedSongs.comparatorProperty().bind(songsTableView.comparatorProperty());
        songsTableView.setItems(sortedSongs);

        // Setup playlists table
        playlistsTableView.setItems(allPlaylists);

        // Setup playlist songs list
        playlistSongsListView.setItems(currentPlaylistSongs);
    }

    private void setupEventHandlers() {
        // Playlist selection handler
        playlistsTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedPlaylist = newValue;
                    loadPlaylistSongs(newValue);
                });

        // Double click to play song from all songs
        songsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSelectedSong(songsTableView.getSelectionModel().getSelectedItem());
            }
        });

        // Double click to play song from playlist
        playlistSongsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSelectedSong(playlistSongsListView.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void loadDataFromDatabase() {
        try {
            // Load songs from database
            List<Song> songs = songManager.getAllSongs();
            allSongs.setAll(songs);

            // Load playlists from database
            List<Playlist> playlists = playlistManager.getAllPlaylists();
            allPlaylists.setAll(playlists);

            System.out.println("Loaded " + songs.size() + " songs and " + playlists.size() + " playlists from database");

        } catch (Exception e) {
            showErrorDialog("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlaylistSongs(Playlist playlist) {
        if (playlist == null) {
            currentPlaylistSongs.clear();
            return;
        }

        try {
            List<Song> songs = playlistManager.getSongsInPlaylist(playlist);
            currentPlaylistSongs.setAll(songs);
        } catch (Exception e) {
            showErrorDialog("Error loading playlist songs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playSelectedSong(Song song) {
        if (song != null) {
            playSong(song);
        }
    }

    /**
     * Smart file finder - tries multiple locations to find music files
     */
    private String findMusicFile(Song song) {
        String originalPath = song.getFilePath();
        System.out.println("Looking for file: " + originalPath);

        // Extract just the filename from the path
        String fileName = new File(originalPath).getName();
        System.out.println("Filename: " + fileName);

        // Try different possible locations
        String[] possibleLocations = {
                // 1. Original absolute path (for your computer)
                originalPath,

                // 2. Relative path from application directory
                "music/" + fileName,
                "../music/" + fileName,
                "./music/" + fileName,

                // 3. In user's home directory
                System.getProperty("user.home") + "/MyTunesMusic/" + fileName,
                System.getProperty("user.home") + "/Music/" + fileName,

                // 4. In current working directory
                System.getProperty("user.dir") + "/music/" + fileName,
                System.getProperty("user.dir") + "/MyTunesMusic/" + fileName,

                // 5. Just the filename in current directory
                fileName
        };

        for (String location : possibleLocations) {
            File file = new File(location);
            System.out.println("Trying: " + location + " -> " + (file.exists() ? "FOUND" : "NOT FOUND"));
            if (file.exists() && file.isFile()) {
                System.out.println("Found file at: " + location);
                return file.toURI().toString();
            }
        }

        // If no file found, return null
        System.out.println("Could not find file: " + fileName);
        return null;
    }

    private void playSong(Song song) {
        try {
            // Safely stop and dispose current media player
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {
                    System.out.println("Warning stopping previous player: " + e.getMessage());
                }
                mediaPlayer.dispose();
                mediaPlayer = null;
            }

            // Find the music file using smart location detection
            String mediaUri = findMusicFile(song);

            if (mediaUri == null) {
                showErrorDialog("Music file not found: " + new File(song.getFilePath()).getName() +
                        "\n\nPlease make sure the music files are in one of these locations:\n" +
                        "- 'music' folder next to the application\n" +
                        "- Your home directory/MyTunesMusic/\n" +
                        "- Same folder as the application");
                return;
            }

            System.out.println("Playing from: " + mediaUri);

            Media media = new Media(mediaUri);
            mediaPlayer = new MediaPlayer(media);

            // Set volume
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);

            // Set up event handlers
            mediaPlayer.setOnReady(() -> {
                nowPlayingLabel.setText("Now Playing: " + song.getTitle() + " - " + song.getArtist());
                setupProgressTracking();
                playMusic();
            });

            mediaPlayer.setOnError(() -> {
                System.out.println("Media error: " + mediaPlayer.getError());
                showErrorDialog("Could not play: " + song.getTitle() +
                        ". Error: " + (mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "Unknown error"));
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Song finished playing");
                stopMusic();
            });

        } catch (Exception e) {
            showErrorDialog("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupProgressTracking() {
        if (mediaPlayer == null) return;

        // Update progress bar
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                if (totalDuration != null && totalDuration.greaterThan(Duration.ZERO)) {
                    double progress = newValue.toSeconds() / totalDuration.toSeconds();
                    progressBar.setProgress(progress);
                    currentTimeLabel.setText(formatTime(newValue));
                }
            } catch (Exception e) {
                System.out.println("Error updating progress: " + e.getMessage());
            }
        });

        // Set total duration
        mediaPlayer.setOnReady(() -> {
            try {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                if (totalDuration != null) {
                    totalTimeLabel.setText(formatTime(totalDuration));
                }
            } catch (Exception e) {
                System.out.println("Error setting total duration: " + e.getMessage());
            }
        });
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Media control methods
    @FXML
    private void playMusic() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.play();
                playButton.setDisable(true);
                pauseButton.setDisable(false);
            } catch (Exception e) {
                showErrorDialog("Error playing music: " + e.getMessage());
            }
        } else {
            showErrorDialog("No song selected to play");
        }
    }

    @FXML
    private void pauseMusic() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
                playButton.setDisable(false);
                pauseButton.setDisable(true);
            } catch (Exception e) {
                showErrorDialog("Error pausing music: " + e.getMessage());
            }
        }
    }

    @FXML
    private void stopMusic() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                playButton.setDisable(false);
                pauseButton.setDisable(true);
                progressBar.setProgress(0);
                currentTimeLabel.setText("00:00");
            } catch (Exception e) {
                showErrorDialog("Error stopping music: " + e.getMessage());
            }
        }
    }

    // Filter method
    @FXML
    private void applyFilter() {
        String filterText = filterField.getText().trim();
        if (filterText.isEmpty()) {
            filteredSongs.setPredicate(song -> true);
            filterButton.setText("Filter");
        } else {
            filteredSongs.setPredicate(song ->
                    song.getTitle().toLowerCase().contains(filterText.toLowerCase()) ||
                            song.getArtist().toLowerCase().contains(filterText.toLowerCase())
            );
            filterButton.setText("Clear");
        }
    }

    // Playlist methods
    @FXML
    private void createNewPlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/demo/GUI/PlaylistEditor.fxml"));
            Parent root = loader.load();

            PlaylistEditorController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("New Playlist");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh playlists after dialog closes
            loadDataFromDatabase();

        } catch (Exception e) {
            showErrorDialog("Error creating playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editPlaylist() {
        Playlist selected = playlistsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a playlist to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/demo/GUI/PlaylistEditor.fxml"));
            Parent root = loader.load();

            PlaylistEditorController controller = loader.getController();
            controller.setPlaylist(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Playlist: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh playlists after dialog closes
            loadDataFromDatabase();

        } catch (Exception e) {
            showErrorDialog("Error editing playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deletePlaylist() {
        Playlist selected = playlistsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a playlist to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Playlist");
        confirmation.setHeaderText("Are you sure you want to delete this playlist?");
        confirmation.setContentText("Playlist: " + selected.getName());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    playlistManager.deletePlaylist(selected);
                    allPlaylists.remove(selected);
                    showInfoDialog("Success", "Playlist '" + selected.getName() + "' deleted successfully");

                    // Clear the playlist songs if this was the selected playlist
                    if (selectedPlaylist != null && selectedPlaylist.getId() == selected.getId()) {
                        selectedPlaylist = null;
                        currentPlaylistSongs.clear();
                    }
                } catch (Exception e) {
                    showErrorDialog("Error deleting playlist: " + e.getMessage());
                }
            }
        });
    }

    // Song methods
    @FXML
    private void createNewSong() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/demo/GUI/SongEditor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("New Song");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh songs after dialog closes
            loadDataFromDatabase();

        } catch (Exception e) {
            showErrorDialog("Error creating song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editSong() {
        Song selected = songsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a song to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/demo/GUI/SongEditor.fxml"));
            Parent root = loader.load();

            SongEditorController controller = loader.getController();
            controller.setSong(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Song: " + selected.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh songs after dialog closes
            loadDataFromDatabase();

        } catch (Exception e) {
            showErrorDialog("Error editing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteSong() {
        Song selected = songsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a song to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Song");
        confirmation.setHeaderText("Are you sure you want to delete this song?");
        confirmation.setContentText("Song: " + selected.getTitle() + " - " + selected.getArtist());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    songManager.deleteSong(selected);
                    allSongs.remove(selected);
                    showInfoDialog("Success", "Song '" + selected.getTitle() + "' deleted successfully");
                } catch (Exception e) {
                    showErrorDialog("Error deleting song: " + e.getMessage());
                }
            }
        });
    }

    // Playlist song management methods
    @FXML
    private void addSongToPlaylist() {
        Song selectedSong = songsTableView.getSelectionModel().getSelectedItem();
        Playlist selectedPlaylist = playlistsTableView.getSelectionModel().getSelectedItem();

        if (selectedSong == null || selectedPlaylist == null) {
            showErrorDialog("Please select both a song and a playlist");
            return;
        }

        try {
            playlistManager.addSongToPlaylist(selectedPlaylist, selectedSong);
            loadPlaylistSongs(selectedPlaylist); // Refresh the playlist songs
            showInfoDialog("Success", "Added '" + selectedSong.getTitle() + "' to '" + selectedPlaylist.getName() + "'");
        } catch (Exception e) {
            showErrorDialog("Error adding song to playlist: " + e.getMessage());
        }
    }

    @FXML
    private void moveSongUp() {
        Song selectedSong = playlistSongsListView.getSelectionModel().getSelectedItem();
        if (selectedSong == null || selectedPlaylist == null) {
            showErrorDialog("Please select a song from the playlist to move");
            return;
        }
        showInfoDialog("Move Up", "Move up functionality would be implemented here");
    }

    @FXML
    private void moveSongDown() {
        Song selectedSong = playlistSongsListView.getSelectionModel().getSelectedItem();
        if (selectedSong == null || selectedPlaylist == null) {
            showErrorDialog("Please select a song from the playlist to move");
            return;
        }
        showInfoDialog("Move Down", "Move down functionality would be implemented here");
    }

    @FXML
    private void removeSongFromPlaylist() {
        Song selectedSong = playlistSongsListView.getSelectionModel().getSelectedItem();
        if (selectedSong == null || selectedPlaylist == null) {
            showErrorDialog("Please select a song from the playlist to remove");
            return;
        }

        try {
            playlistManager.removeSongFromPlaylist(selectedPlaylist, selectedSong);
            currentPlaylistSongs.remove(selectedSong);
            showInfoDialog("Success", "Removed '" + selectedSong.getTitle() + "' from playlist");
        } catch (Exception e) {
            showErrorDialog("Error removing song from playlist: " + e.getMessage());
        }
    }

    @FXML
    private void closeApplication() {
        shutdown();
        Stage stage = (Stage) songsTableView.getScene().getWindow();
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

    public void shutdown() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                System.out.println("Error stopping media player on shutdown: " + e.getMessage());
            }
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}