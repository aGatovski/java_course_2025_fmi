package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class MissionsLoader {
    public static List<Mission> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null!");
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                .skip(1)
                .map(Mission::of)
                .toList();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Error reading", e);
        }
    }
}