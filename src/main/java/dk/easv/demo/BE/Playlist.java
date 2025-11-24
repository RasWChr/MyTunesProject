package dk.easv.demo.BE;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Playlist {
    private int id;
    private String name;

    // Properties for table binding
    private StringProperty nameProperty;
    private IntegerProperty songCountProperty;
    private StringProperty totalDurationProperty;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.nameProperty = new SimpleStringProperty(name);
        this.songCountProperty = new SimpleIntegerProperty(0);
        this.totalDurationProperty = new SimpleStringProperty("00:00");
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.nameProperty.set(name);
    }

    // Property getters for table binding
    public StringProperty nameProperty() { return nameProperty; }
    public IntegerProperty songCountProperty() { return songCountProperty; }
    public StringProperty totalDurationProperty() { return totalDurationProperty; }

    // Regular getters for properties
    public int getSongCount() { return songCountProperty.get(); }
    public String getTotalDuration() { return totalDurationProperty.get(); }

    // Setters for properties
    public void setSongCount(int count) { this.songCountProperty.set(count); }
    public void setTotalDuration(String duration) { this.totalDurationProperty.set(duration); }

    @Override
    public String toString() {
        return name;
    }
}