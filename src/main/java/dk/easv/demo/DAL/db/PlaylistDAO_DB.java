package dk.easv.demo.DAL.db;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;
import dk.easv.demo.DAL.IPlaylistDataAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_DB implements IPlaylistDataAccess {

    private DBConnector dbConnector;

    public PlaylistDAO_DB() {
        dbConnector = new DBConnector();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws SQLException {
        List<Playlist> allPlaylists = new ArrayList<>();

        String sql = "SELECT * FROM playlists";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                Playlist playlist = new Playlist(id, name);
                allPlaylists.add(playlist);
            }
        }
        return allPlaylists;
    }

    @Override
    public Playlist createPlaylist(String name) throws SQLException {
        String sql = "INSERT INTO playlists (name) VALUES (?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Playlist(id, name);
                }
            }
        }
        return null;
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws SQLException {
        String sql = "UPDATE playlists SET name = ? WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws SQLException {
        // First delete from playlist_songs
        String deleteSongsSql = "DELETE FROM playlist_songs WHERE playlist_id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSongsSql)) {
            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
        }

        // Then delete the playlist
        String deletePlaylistSql = "DELETE FROM playlists WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deletePlaylistSql)) {
            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId, int position) throws SQLException {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id, position) VALUES (?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.setInt(3, position);
            stmt.executeUpdate();
        }
    }

    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
        List<Song> songs = new ArrayList<>();

        String sql = "SELECT s.* FROM songs s " +
                "JOIN playlist_songs ps ON s.id = ps.song_id " +
                "WHERE ps.playlist_id = ? " +
                "ORDER BY ps.position";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String artist = rs.getString("artist");
                    String category = rs.getString("category");
                    int duration = rs.getInt("duration");
                    String filePath = rs.getString("file_path");

                    Song song = new Song(id, title, artist, category, duration, filePath);
                    songs.add(song);
                }
            }
        }
        return songs;
    }
}