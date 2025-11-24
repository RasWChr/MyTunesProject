package dk.easv.demo.DAL;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;
import java.sql.SQLException;
import java.util.List;

public interface IPlaylistDataAccess {
    List<Playlist> getAllPlaylists() throws SQLException;
    Playlist createPlaylist(String name) throws SQLException;
    void updatePlaylist(Playlist playlist) throws SQLException;
    void deletePlaylist(Playlist playlist) throws SQLException;
    void addSongToPlaylist(int playlistId, int songId, int position) throws SQLException;
    void removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
    List<Song> getSongsInPlaylist(int playlistId) throws SQLException;
}