package dk.easv.demo.GUI.Model;

import dk.easv.demo.BE.Playlist;
import dk.easv.demo.BE.Song;

import java.util.ArrayList;
import java.util.List;

public class MockMainModel {
    private List<Song> allSongs;
    private List<Playlist> allPlaylists;

    public MockMainModel() {
        allSongs = new ArrayList<>();
        allPlaylists = new ArrayList<>();
        createMockData();
    }

    private void createMockData() {
        // Create mock songs - duration must be int (seconds), not String
        allSongs.add(new Song(1, "Hello", "Adele", "Pop", 295, "C:\\MyTunesMusic\\Adele Hello.mp3"));
        allSongs.add(new Song(2, "Fly Away", "Michael Jackson", "Pop", 214, "C:\\MyTunesMusic\\Fly Away.mp3"));
        allSongs.add(new Song(3, "Hard Time", "Seinabo Sey", "Pop", 271, "C:\\MyTunesMusic\\Seinabo Sey Hard Time.mp3"));
        allSongs.add(new Song(4, "Chasing Cars", "Snow Patrol", "Pop", 214, "C:\\MyTunesMusic\\Snow Patrol Chasing Cars.mp3"));
        allSongs.add(new Song(5, "Fire", "Springsteen", "Rock", 162, "C:\\MyTunesMusic\\Fire.mp3"));

        // Create mock playlists
        allPlaylists.add(new Playlist(1, "Party"));
        allPlaylists.add(new Playlist(2, "Workout"));
        allPlaylists.add(new Playlist(3, "Relax"));
        allPlaylists.add(new Playlist(4, "Favorites"));
    }

    public List<Song> getAllSongs() {
        return new ArrayList<>(allSongs);
    }

    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(allPlaylists);
    }

    public void addSongToPlaylist(Playlist playlist, Song song) {
        // Mock implementation - in real app this would update the database
        System.out.println("Mock: Added '" + song.getTitle() + "' to playlist '" + playlist.getName() + "'");
    }

    public Playlist createPlaylist(String name) {
        int newId = allPlaylists.size() + 1;
        Playlist newPlaylist = new Playlist(newId, name);
        allPlaylists.add(newPlaylist);
        return newPlaylist;
    }

    public List<Song> getSongsInPlaylist(Playlist playlist) {
        // Return some mock songs for the playlist
        List<Song> playlistSongs = new ArrayList<>();
        if (playlist.getName().equals("Party")) {
            playlistSongs.add(allSongs.get(0)); // Hello
            playlistSongs.add(allSongs.get(1)); // Fly Away
        } else if (playlist.getName().equals("Workout")) {
            playlistSongs.add(allSongs.get(2)); // Hard Time
            playlistSongs.add(allSongs.get(4)); // Fire
        }
        return playlistSongs;
    }
}