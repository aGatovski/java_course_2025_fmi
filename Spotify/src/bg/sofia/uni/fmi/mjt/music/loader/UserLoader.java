package bg.sofia.uni.fmi.mjt.music.loader;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.User;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserLoader {

    public static ConcurrentMap<String, User> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader should not be null");
        }

        var lineReader = new BufferedReader(reader);
        return lineReader.lines()
            .skip(1)
            .map(UserLoader::parseUserSafely)
            .filter(Objects::nonNull)
            .collect(Collectors.toConcurrentMap(User::email, Function.identity()));
    }

    private static User parseUserSafely(String line) {
        try {
            return User.of(line);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Logger.logError("Skipping invalid user entry: " + line, e, null);
            return null;
        }
    }
}
