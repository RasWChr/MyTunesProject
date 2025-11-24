package dk.easv.demo.GUI.Model;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;
import dk.easv.demo.BLL.SongManager;
import dk.easv.demo.BLL.PlaylistManager;

import java.sql.SQLException;
import java.util.List;

public class MainModel {
    private SongManager songManager;
    private PlaylistManager playlistManager;

    public MainModel() {
        songManager = new SongManager();
        playlistManager = new PlaylistManager();
    }

    public List<Song> getAllSongs() throws SQLException {
        return songManager.getAllSongs();
    }

    public List<Playlist> getAllPlaylists() throws SQLException {
        return playlistManager.getAllPlaylists();
    }

    public Playlist createPlaylist(String name) throws SQLException {
        return playlistManager.createPlaylist(name);
    }

    public void addSongToPlaylist(Playlist playlist, Song song) throws SQLException {
        playlistManager.addSongToPlaylist(playlist, song);
    }

    public List<Song> getSongsInPlaylist(Playlist playlist) throws SQLException {
        return playlistManager.getSongsInPlaylist(playlist);
    }
}