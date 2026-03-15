package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class RocketsLoader {
    public static List<Rocket> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null!");
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                .skip(1)
                .map(Rocket::of)
                .toList();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Error reading", e);
        }
    }
}