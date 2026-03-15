package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.loader.ResourcesLoader;
import bg.sofia.uni.fmi.mjt.music.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.model.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class InMemoryPlaylistRepository implements PlaylistRepository {

    private final ConcurrentMap<String, Playlist> playlistsById; // id:playlist
    private final Path playlistsDirectory;

    public InMemoryPlaylistRepository() {
        this.playlistsById = ResourcesLoader.loadPlaylists();
        this.playlistsDirectory = ResourcesLoader.getPlaylistsDirectory();

        if (!Files.exists(playlistsDirectory)) {
            try {
                Files.createDirectories(playlistsDirectory);
            } catch (IOException e) {
                System.err.println("Failed to create playlists directory: " + e.getMessage());
            }
        }

        System.out.println("Loaded " + playlistsById.size() + " playlists");
    }

    // Constructor for testing
    InMemoryPlaylistRepository(ConcurrentMap<String, Playlist> playlistsById, Path playlistsDirectory) {
        this.playlistsById = playlistsById;
        this.playlistsDirectory = playlistsDirectory;
    }

    @Override
    public boolean createPlaylist(String name, String owner) {
        if (findPlaylist(name, owner).isPresent()) {
            return false;
        }

        Playlist playlist = new Playlist(name, owner);
        playlist.setSongIds(new ArrayList<>());

        playlistsById.put(playlist.id(), playlist);

        savePlaylistFile(playlist);
        saveIndex();

        return true;
    }

    @Override
    public String addSongToPlaylist(String playlistName, String songQuery, String owner,
                                    SongRepository songRepository) {
        Optional<Playlist> playlistOpt = findPlaylist(playlistName, owner);
        if (playlistOpt.isEmpty()) {
            return "Playlist not found: " + playlistName;
        }

        Playlist playlist = playlistOpt.get();
        loadPlaylistIfNeeded(playlist);

        Optional<Song> songOpt = songRepository.findSong(songQuery);
        if (songOpt.isEmpty()) {
            return "Song not found: " + songQuery;
        }

        Song song = songOpt.get();
        if (playlist.songIds().contains(song.id())) {
            return "Song already in playlist";
        }

        playlist.addSongId(song.id());
        savePlaylistFile(playlist);

        return "Added \"" + song.displayName() + "\" to playlist \"" + playlistName + "\"";
    }

    @Override
    public String showPlaylist(String playlistName, String owner, SongRepository songRepository) {
        Optional<Playlist> playlistOpt = findPlaylist(playlistName, owner);
        if (playlistOpt.isEmpty()) {
            return "Playlist not found: " + playlistName;
        }

        Playlist playlist = playlistOpt.get();
        loadPlaylistIfNeeded(playlist);

        StringBuilder sb = new StringBuilder();
        sb.append("Playlist: ").append(playlist.name()).append("\n");
        sb.append("Owner: ").append(playlist.owner()).append("\n");
        sb.append("Songs (").append(playlist.songIds().size()).append("):\n");

        int index = 1;
        for (int songId : playlist.songIds()) {
            Optional<Song> songOpt = songRepository.findSongById(songId);
            if (songOpt.isPresent()) {
                sb.append(" ").append(index++).append(". ").append(songOpt.get().displayName()).append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public Optional<Playlist> findPlaylist(String name, String owner) {
        return playlistsById.values().stream()
            .filter(p -> p.name().equalsIgnoreCase(name)
                && p.owner().equalsIgnoreCase(owner))
            .findFirst();
    }

    private void loadPlaylistIfNeeded(Playlist playlist) {
        if (playlist.isLoaded()) {
            return;
        }

        Path filePath = playlistsDirectory.resolve(playlist.filename());
        List<Integer> songIds = new ArrayList<>();

        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    songIds.add(Integer.parseInt(line));
                }
            } catch (IOException e) {
                Logger.logError("Error loading playlist songs from file", e, null);
                System.err.println("Error loading playlist songs from file: " + e.getMessage());
            }
        }

        playlist.setSongIds(songIds);
    }

    private void savePlaylistFile(Playlist playlist) {
        Path filePath = playlistsDirectory.resolve(playlist.filename());

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("# playlist: " + playlist.name());
            writer.newLine();
            writer.write("# owner: " + playlist.owner());
            writer.newLine();

            for (int songId : playlist.songIds()) {
                writer.write(String.valueOf(songId));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving playlist file: " + e.getMessage());
        }
    }

    private void saveIndex() {
        ResourcesLoader.savePlaylists(playlistsById);
    }
}
