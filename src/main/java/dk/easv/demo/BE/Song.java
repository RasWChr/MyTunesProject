package dk.easv.demo.BE;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String category;
    private int duration; // in seconds
    private String filePath;

    // Properties for table binding
    private StringProperty titleProperty;
    private StringProperty artistProperty;
    private StringProperty categoryProperty;
    private StringProperty durationProperty;

    public Song(int id, String title, String artist, String category, int duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.duration = duration;
        this.filePath = filePath;

        this.titleProperty = new SimpleStringProperty(title);
        this.artistProperty = new SimpleStringProperty(artist);
        this.categoryProperty = new SimpleStringProperty(category);
        this.durationProperty = new SimpleStringProperty(formatDuration());
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.titleProperty.set(title);
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) {
        this.artist = artist;
        this.artistProperty.set(artist);
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
        this.categoryProperty.set(category);
    }

    public int getDuration() { return duration; }
    public void setDuration(int duration) {
        this.duration = duration;
        this.durationProperty.set(formatDuration());
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    // Property getters for table binding
    public StringProperty titleProperty() { return titleProperty; }
    public StringProperty artistProperty() { return artistProperty; }
    public StringProperty categoryProperty() { return categoryProperty; }
    public StringProperty durationProperty() { return durationProperty; }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + formatDuration() + ")";
    }

    private String formatDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}