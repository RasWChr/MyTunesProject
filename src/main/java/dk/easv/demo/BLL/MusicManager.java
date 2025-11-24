package dk.easv.demo.BLL;

import dk.easv.demo.BE.Song;
import dk.easv.demo.DAL.ISongDataAccess;
import dk.easv.demo.DAL.db.SongDAO_DB;

import java.sql.SQLException;
import java.util.List;

public class MusicManager {
    private ISongDataAccess songDAO;

    public MusicManager() {
        songDAO = new SongDAO_DB();
    }

    public List<Song> getAllSongs() throws SQLException {
        return songDAO.getAllSongs();
    }

    public Song createSong(String title, String artist, String category, int duration, String filePath) throws SQLException {
        Song song = new Song(0, title, artist, category, duration, filePath);
        return songDAO.createSong(song);
    }

    public void updateSong(Song song) throws SQLException {
        songDAO.updateSong(song);
    }

    public void deleteSong(Song song) throws SQLException {
        songDAO.deleteSong(song);
    }

    public Song getSongById(int songId) throws SQLException {
        return songDAO.getSongById(songId);
    }
}