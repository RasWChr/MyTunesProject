package dk.easv.demo.BLL;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;
import dk.easv.demo.DAL.IPlaylistDataAccess;
import dk.easv.demo.DAL.db.PlaylistDAO_DB;

import java.sql.SQLException;
import java.util.List;

public class PlaylistManager {
    private IPlaylistDataAccess playlistDAO;

    public PlaylistManager() {
        playlistDAO = new PlaylistDAO_DB();
    }

    public List<Playlist> getAllPlaylists() throws SQLException {
        return playlistDAO.getAllPlaylists();
    }

    public Playlist createPlaylist(String name) throws SQLException {
        return playlistDAO.createPlaylist(name);
    }

    public void updatePlaylist(Playlist playlist) throws SQLException {
        playlistDAO.updatePlaylist(playlist);
    }

    public void deletePlaylist(Playlist playlist) throws SQLException {
        playlistDAO.deletePlaylist(playlist);
    }

    public void addSongToPlaylist(Playlist playlist, Song song) throws SQLException {
        // Get current count of songs in playlist to determine position
        List<Song> currentSongs = getSongsInPlaylist(playlist);
        int position = currentSongs.size();
        playlistDAO.addSongToPlaylist(playlist.getId(), song.getId(), position);
    }

    public void removeSongFromPlaylist(Playlist playlist, Song song) throws SQLException {
        playlistDAO.removeSongFromPlaylist(playlist.getId(), song.getId());
    }

    public List<Song> getSongsInPlaylist(Playlist playlist) throws SQLException {
        return playlistDAO.getSongsInPlaylist(playlist.getId());
    }
}