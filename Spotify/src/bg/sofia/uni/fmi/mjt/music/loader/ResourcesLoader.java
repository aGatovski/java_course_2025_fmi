package bg.sofia.uni.fmi.mjt.music.loader;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.model.Song;
import bg.sofia.uni.fmi.mjt.music.model.User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ResourcesLoader {

    private static final Path SONGS_DIR = Path.of("resources", "songs");
    private static final Path SONGS_CSV = Path.of("resources", "songs.csv");

    private static final Path USERS_CSV = Path.of("resources", "users.csv");

    private static final Path PLAYLISTS_DIR = Path.of("resources", "playlists");
    private static final Path PLAYLISTS_CSV = Path.of("resources", "playlists.csv");

    public static ConcurrentMap<Integer, Song> loadSongs() {
        try (var reader = Files.newBufferedReader(SONGS_CSV)) {
            return SongLoader.load(reader);
        } catch (IOException e) {
            Logger.logError("Error loading songs", e, null);
            throw new UncheckedIOException("Error loading songs", e);
        }
    }

    public static ConcurrentMap<String, User> loadUsers() {
        try (var reader = Files.newBufferedReader(USERS_CSV)) {
            return UserLoader.load(reader);
        } catch (IOException e) {
            Logger.logError("Error loading users", e, null);
            throw new UncheckedIOException("Error loading users", e);
        }
    }

    public static ConcurrentMap<String, Playlist> loadPlaylists() {
        try (var reader = Files.newBufferedReader(PLAYLISTS_CSV)) {
            return PlaylistLoader.load(reader);
        } catch (IOException e) {
            Logger.logError("Error loading playlists", e, null);
            System.err.println("Error loading playlists: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }

    public static void saveSongs(Map<Integer, Song> songs) {
        try (var writer = Files.newBufferedWriter(SONGS_CSV)) {
            writer.write("id,title,artist,filename,play_count");
            writer.newLine();

            for (Song song : songs.values()) {
                writer.write(song.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.logError("Error saving songs", e, null);
            System.err.println("Error saving songs: " + e.getMessage());
        }
    }

    public static void saveUsers(Map<String, User> users) {
        try (var writer = Files.newBufferedWriter(USERS_CSV)) {
            writer.write("email,password");
            writer.newLine();

            for (User user : users.values()) {
                writer.write(user.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.logError("Error saving users", e, null);
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static void savePlaylists(Map<String, Playlist> playlists) {
        try (var writer = Files.newBufferedWriter(PLAYLISTS_CSV)) {
            writer.write("id,name,owner,filename");
            writer.newLine();

            for (Playlist playlist : playlists.values()) {
                writer.write(playlist.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.logError("Error saving playlists", e, null);
            System.err.println("Error saving playlists: " + e.getMessage());
        }
    }

    public static Path getSongsDirectory() {
        return SONGS_DIR;
    }

    public static Path getPlaylistsDirectory() {
        return PLAYLISTS_DIR;
    }
}
