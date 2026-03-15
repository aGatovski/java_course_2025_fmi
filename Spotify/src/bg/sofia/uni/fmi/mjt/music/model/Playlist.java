package bg.sofia.uni.fmi.mjt.music.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Playlist {

    private static final String DELIMITER = ",";

    private static final int ID_IDX = 0;
    private static final int NAME_IDX = 1;
    private static final int OWNER_IDX = 2;
    private static final int FILENAME_IDX = 3;

    private final String id;
    private final String name;
    private final String owner;
    private final String filename;
    private List<Integer> songIds;

    private Playlist(String id, String name, String owner, String filename) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Playlist name cannot be null or empty");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner cannot be null or empty");
        }
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.owner = owner;
        this.filename = filename;
        this.songIds = null;
    }

    public Playlist(String name, String owner) {
        String id = String.valueOf(UUID.randomUUID());
        this(id, name, owner, id + ".txt");
    }

    public static Playlist of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Input line cannot be null or empty");
        }

        final String[] attributes = line.split(DELIMITER);

        String id = attributes[ID_IDX].trim();
        String name = attributes[NAME_IDX].trim();
        String artist = attributes[OWNER_IDX].trim();
        String filename = attributes[FILENAME_IDX].trim();

        return new Playlist(id, name, artist, filename);
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%s", id, name, owner, filename);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String owner() {
        return owner;
    }

    public String filename() {
        return filename;
    }

    public List<Integer> songIds() {
        return songIds == null ? List.of() : Collections.unmodifiableList(songIds);
    }

    public boolean isLoaded() {
        return songIds != null;
    }

    public void setSongIds(List<Integer> songIds) {
        this.songIds = new ArrayList<>(songIds);
    }

    public void addSongId(int songId) {
        if (songIds == null) {
            songIds = new ArrayList<>();
        }

        if (!songIds.contains(songId)) {
            songIds.add(songId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
