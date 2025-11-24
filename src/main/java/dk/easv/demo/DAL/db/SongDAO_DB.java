package dk.easv.demo.DAL.db;

import dk.easv.demo.BE.Song;
import dk.easv.demo.DAL.ISongDataAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO_DB implements ISongDataAccess {

    private DBConnector dbConnector;

    public SongDAO_DB() {
        dbConnector = new DBConnector();
    }

    @Override
    public List<Song> getAllSongs() throws SQLException {
        List<Song> allSongs = new ArrayList<>();

        String sql = "SELECT * FROM songs";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String category = rs.getString("category");
                int duration = rs.getInt("duration");
                String filePath = rs.getString("file_path");

                Song song = new Song(id, title, artist, category, duration, filePath);
                allSongs.add(song);
            }
        }
        return allSongs;
    }

    @Override
    public Song createSong(Song song) throws SQLException {
        String sql = "INSERT INTO songs (title, artist, category, duration, file_path) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getCategory());
            stmt.setInt(4, song.getDuration());
            stmt.setString(5, song.getFilePath());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Song(id, song.getTitle(), song.getArtist(), song.getCategory(), song.getDuration(), song.getFilePath());
                }
            }
        }
        return null;
    }

    @Override
    public void updateSong(Song song) throws SQLException {
        String sql = "UPDATE songs SET title = ?, artist = ?, category = ?, duration = ?, file_path = ? WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getCategory());
            stmt.setInt(4, song.getDuration());
            stmt.setString(5, song.getFilePath());
            stmt.setInt(6, song.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteSong(Song song) throws SQLException {
        String sql = "DELETE FROM songs WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, song.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public Song getSongById(int songId) throws SQLException {
        String sql = "SELECT * FROM songs WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, songId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String artist = rs.getString("artist");
                    String category = rs.getString("category");
                    int duration = rs.getInt("duration");
                    String filePath = rs.getString("file_path");

                    return new Song(id, title, artist, category, duration, filePath);
                }
            }
        }
        return null;
    }
}