package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.model.Song;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface SongRepository {

    List<Song> search(String keyword);

    Optional<Song> findSong(String searchTerm);

    Optional<Song> findSongById(int id);

    List<Song> getTopSongs(int count);

    void incrementPlayCount(Song song);

    Path getSongPath(Song song);

    boolean songFileExists(Song song);

    void save();
}
