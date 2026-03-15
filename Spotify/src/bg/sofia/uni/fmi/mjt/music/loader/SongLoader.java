package bg.sofia.uni.fmi.mjt.music.loader;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.Song;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SongLoader {

    public static ConcurrentMap<Integer, Song> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader should not be null");
        }

        var lineReader = new BufferedReader(reader);
        return lineReader.lines()
            .skip(1)
            .map(SongLoader::parseSongSafely)
            .filter(Objects::nonNull)
            .collect(Collectors.toConcurrentMap(Song::id, Function.identity()));
    }

    private static Song parseSongSafely(String line) {
        try {
            return Song.of(line);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Logger.logError("Skipping invalid song entry: " + line, e, null);
            return null;
        }
    }
}
