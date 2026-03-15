package bg.sofia.uni.fmi.mjt.music.loader;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.Playlist;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlaylistLoader {

    public static ConcurrentMap<String, Playlist> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader should not be null");
        }

        var lineReader = new BufferedReader(reader);
        return lineReader.lines()
            .skip(1)
            .map(PlaylistLoader::parsePlaylistSafely)
            .filter(Objects::nonNull)
            .collect(Collectors.toConcurrentMap(Playlist::id, Function.identity()));
    }

    private static Playlist parsePlaylistSafely(String line) {
        try {
            return Playlist.of(line);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Logger.logError("Skipping invalid playlist entry: " + line, e, null);
            return null;
        }
    }
}
