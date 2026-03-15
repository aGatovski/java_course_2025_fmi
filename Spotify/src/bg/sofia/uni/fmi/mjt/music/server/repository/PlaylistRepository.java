package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.model.Playlist;

import java.util.Optional;

public interface PlaylistRepository {

    boolean createPlaylist(String name, String owner);

    String addSongToPlaylist(String playlistName, String songQuery, String owner, SongRepository songRepository);

    String showPlaylist(String playlistName, String owner, SongRepository songRepository);

    Optional<Playlist> findPlaylist(String name, String owner);
}
