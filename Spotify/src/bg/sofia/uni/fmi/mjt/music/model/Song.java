package bg.sofia.uni.fmi.mjt.music.model;

import java.util.Objects;

public record Song(int id, String title, String artist, String filename, int playCount) {

    private static final String DELIMITER = ",";

    private static final int ID_IDX = 0;
    private static final int TITLE_IDX = 1;
    private static final int ARTIST_IDX = 2;
    private static final int FILENAME_IDX = 3;
    private static final int PLAYCOUNT_IDX = 4;

    public Song {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (artist == null || artist.isBlank()) {
            throw new IllegalArgumentException("Artist cannot be null or blank");
        }
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or blank");
        }
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        if (playCount < 0) {
            throw new IllegalArgumentException("Play count cannot be negative");
        }
    }

    public static Song of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Input line cannot be null or empty");
        }

        final String[] attributes = line.split(DELIMITER);

        int id = Integer.parseInt(attributes[ID_IDX].trim());
        String title = attributes[TITLE_IDX].trim();
        String artist = attributes[ARTIST_IDX].trim();
        String filename = attributes[FILENAME_IDX].trim();
        int playCount = Integer.parseInt(attributes[PLAYCOUNT_IDX].trim());

        return new Song(id, title, artist, filename, playCount);
    }

    public String displayName() {
        return artist + " - " + title;
    }

    public String toCsv() {
        return String.format("%d,%s,%s,%s,%d", id, title, artist, filename, playCount);
    }

    public Song withIncrementedPlayCount() {
        return new Song(id, title, artist, filename, playCount + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
