    package dk.easv.demo.GUI.Controller;

    import dk.easv.demo.BE.Playlist;
    import dk.easv.demo.BE.Song;
    import dk.easv.demo.BLL.SongManager;
    import dk.easv.demo.BLL.PlaylistManager;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.*;

    import java.net.URL;
    import java.sql.SQLException;
    import java.util.List;
    import java.util.ResourceBundle;

    public class PlaylistEditorController implements Initializable {

        @FXML private ListView<Song> availableSongsListView;
        @FXML private ListView<Song> playlistSongsListView;
        @FXML private TextField playlistNameField;
        @FXML private Button saveButton, cancelButton, addButton, removeButton;

        private ObservableList<Song> availableSongs;
        private ObservableList<Song> playlistSongs;
        private SongManager songManager;
        private PlaylistManager playlistManager;
        private Playlist currentPlaylist;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            try {
                songManager = new SongManager();
                playlistManager = new PlaylistManager();

                availableSongs = FXCollections.observableArrayList();
                playlistSongs = FXCollections.observableArrayList();

                setupListViews();
                loadAvailableSongs();

            } catch (Exception e) {
                showErrorDialog("Error initializing playlist editor: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void setupListViews() {
            availableSongsListView.setItems(availableSongs);
            playlistSongsListView.setItems(playlistSongs);
        }

        private void loadAvailableSongs() {
            try {
                List<Song> songs = songManager.getAllSongs();
                availableSongs.setAll(songs); // Use setAll() to convert List to ObservableList
            } catch (SQLException e) {
                showErrorDialog("Error loading songs: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void setPlaylist(Playlist playlist) {
            this.currentPlaylist = playlist;
            if (playlist != null) {
                playlistNameField.setText(playlist.getName());
                loadPlaylistSongs(playlist);
            }
        }

        private void loadPlaylistSongs(Playlist playlist) {
            try {
                List<Song> songs = playlistManager.getSongsInPlaylist(playlist);
                playlistSongs.setAll(songs); // Use setAll() to convert List to ObservableList
            } catch (SQLException e) {
                showErrorDialog("Error loading playlist songs: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        private void addSongToPlaylist() {
            Song selectedSong = availableSongsListView.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                playlistSongs.add(selectedSong);
                availableSongs.remove(selectedSong);
            }
        }

        @FXML
        private void removeSongFromPlaylist() {
            Song selectedSong = playlistSongsListView.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                playlistSongs.remove(selectedSong);
                availableSongs.add(selectedSong);
            }
        }

        @FXML
        private void savePlaylist() {
            String playlistName = playlistNameField.getText().trim();
            if (playlistName.isEmpty()) {
                showErrorDialog("Please enter a playlist name");
                return;
            }

            try {
                if (currentPlaylist == null) {
                    // Create new playlist
                    Playlist newPlaylist = playlistManager.createPlaylist(playlistName);

                    // Add songs to the new playlist
                    for (int i = 0; i < playlistSongs.size(); i++) {
                        Song song = playlistSongs.get(i);
                        playlistManager.addSongToPlaylist(newPlaylist, song);
                    }
                    showInfoDialog("Success", "Playlist '" + playlistName + "' created successfully");
                } else {
                    // Update existing playlist
                    currentPlaylist.setName(playlistName);
                    // TODO: Implement playlist update logic
                    showInfoDialog("Success", "Playlist '" + playlistName + "' updated successfully");
                }

                closeWindow();

            } catch (SQLException e) {
                showErrorDialog("Error saving playlist: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        private void cancel() {
            closeWindow();
        }

        private void closeWindow() {
            // Get the current stage and close it
            cancelButton.getScene().getWindow().hide();
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