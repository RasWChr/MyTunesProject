package dk.easv.demo.DAL;

import dk.easv.demo.BE.Song;
import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess {
    List<Song> getAllSongs() throws SQLException;
    Song createSong(Song song) throws SQLException;
    void updateSong(Song song) throws SQLException;
    void deleteSong(Song song) throws SQLException;
    Song getSongById(int songId) throws SQLException;
}