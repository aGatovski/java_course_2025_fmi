package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.loader.ResourcesLoader;
import bg.sofia.uni.fmi.mjt.music.model.Song;
import bg.sofia.uni.fmi.mjt.music.utils.LevenshteinDistance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class InMemorySongRepository implements SongRepository {

    private static final double SIMILARITY_THRESHOLD = 0.8;

    private final ConcurrentMap<Integer, Song> songsById;
    private final Path songsDirectory;

    public InMemorySongRepository() {
        this.songsById = ResourcesLoader.loadSongs();
        this.songsDirectory = ResourcesLoader.getSongsDirectory();
        System.out.println("Loaded " + songsById.size() + " songs");
    }

    // Constructor for testing
    InMemorySongRepository(ConcurrentMap<Integer, Song> songsById, Path songsDirectory) {
        this.songsById = songsById;
        this.songsDirectory = songsDirectory;
    }

    @Override
    public List<Song> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String normalized = keyword.toLowerCase().trim();

        return songsById.values().stream()
            .map(song -> new ScoredSong(song, calculateMatchScore(song, normalized)))
            .filter(scored -> scored.score >= SIMILARITY_THRESHOLD)
            .sorted(Comparator.comparingDouble(ScoredSong::score).reversed())
            .map(ScoredSong::song)
            .toList();
    }

    @Override
    public Optional<Song> findSong(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return Optional.empty();
        }

        String normalized = searchTerm.toLowerCase().trim();

        Optional<Song> exact = songsById.values().stream()
            .filter(song -> song.title().toLowerCase().contains(normalized) ||
                song.artist().toLowerCase().contains(normalized))
            .findFirst();

        if (exact.isPresent()) {
            return exact;
        }

        return songsById.values().stream()
            .map(song -> new ScoredSong(song, calculateMatchScore(song, normalized)))
            .filter(scored -> scored.score >= SIMILARITY_THRESHOLD)
            .max(Comparator.comparingDouble(ScoredSong::score))
            .map(ScoredSong::song);
    }

    @Override
    public List<Song> getTopSongs(int count) {
        if (count <= 0) {
            return List.of();
        }

        return songsById.values().stream()
            .sorted(Comparator.comparingInt(Song::playCount).reversed())
            .limit(count)
            .toList();
    }

    @Override
    public void incrementPlayCount(Song song) {
        songsById.computeIfPresent(song.id(), (id, s) -> s.withIncrementedPlayCount());
    }

    @Override
    public void save() {
        ResourcesLoader.saveSongs(songsById);
    }

    @Override
    public Path getSongPath(Song song) {
        return songsDirectory.resolve(song.filename());
    }

    @Override
    public Optional<Song> findSongById(int id) {
        return Optional.ofNullable(songsById.get(id));
    }

    @Override
    public boolean songFileExists(Song song) {
        return Files.exists(getSongPath(song));
    }

    private double calculateMatchScore(Song song, String keyword) {
        String searchable = (song.title() + " " + song.artist()).toLowerCase();
        if (searchable.contains(keyword)) {
            return 1.0;
        }

        double titleScore = LevenshteinDistance.similarity(song.title().toLowerCase(), keyword);
        double artistScore = LevenshteinDistance.similarity(song.artist().toLowerCase(), keyword);
        double displayScore = LevenshteinDistance.similarity(song.displayName().toLowerCase(), keyword);

        double wordScore = getWordMatchScore(song, keyword);

        return Math.max(Math.max(titleScore, artistScore), Math.max(displayScore, wordScore));
    }

    private double getWordMatchScore(Song song, String keyword) {
        String[] titleWords = song.title().toLowerCase().split("\\s+");
        String[] artistWords = song.artist().toLowerCase().split("\\s+");

        double bestScore = 0;

        for (String word : titleWords) {
            bestScore = Math.max(bestScore, LevenshteinDistance.similarity(word, keyword));
        }

        for (String word : artistWords) {
            bestScore = Math.max(bestScore, LevenshteinDistance.similarity(word, keyword));
        }

        return bestScore;
    }

    private record ScoredSong(Song song, double score) { }
}
