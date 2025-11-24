package dk.easv.demo.BE;

public class PlaylistItem {
    private int playlistId;
    private int songId;
    private int position;

    public PlaylistItem(int playlistId, int songId, int position) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.position = position;
    }

    // Getters
    public int getPlaylistId() { return playlistId; }
    public int getSongId() { return songId; }
    public int getPosition() { return position; }
}