package bg.sofia.uni.fmi.mjt.music.server.command;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.ServerResponse;
import bg.sofia.uni.fmi.mjt.music.model.Song;
import bg.sofia.uni.fmi.mjt.music.model.StreamReadyResponse;
import bg.sofia.uni.fmi.mjt.music.server.repository.PlaylistRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.SongRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.music.server.streaming.StreamingServer;
import bg.sofia.uni.fmi.mjt.music.validation.EmailValidator;
import com.google.gson.Gson;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandExecutor {

    private static final String INVALID_FORMAT_MESSAGE =
        "Invalid command format: \"%s\" expects %d argument(s). Example: \"%s\"";

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String DISCONNECT = "disconnect";
    private static final String SEARCH = "search";
    private static final String TOP_LISTENED = "top";
    private static final String CREATE_PLAYLIST = "create-playlist";
    private static final String ADD_SONG_TO = "add-song-to";
    private static final String SHOW_PLAYLIST = "show-playlist";
    private static final String PLAY = "play";
    private static final String STOP = "stop";

    private static final int REGISTER_ARG_COUNT = 2;
    private static final int LOGIN_ARG_COUNT = 2;
    private static final int DISCONNECT_ARG_COUNT = 0;
    private static final int SEARCH_MIN_ARG_COUNT = 1;
    private static final int TOP_LISTENED_ARG_COUNT = 1;
    private static final int CREATE_PLAYLIST_MIN_ARG_COUNT = 1;
    private static final int ADD_SONG_TO_MIN_ARG_COUNT = 2;
    private static final int SHOW_PLAYLIST_MIN_ARG_COUNT = 1;
    private static final int PLAY_MIN_ARG_COUNT = 1;
    private static final int STOP_ARG_COUNT = 0;

    private static final int EMAIL_ARG_IDX = 0;
    private static final int PASSWORD_ARG_IDX = 1;
    private static final int PLAYLIST_NAME_ARG_IDX = 0;
    private static final int TOP_NUMBER_ARG_IDX = 0;

    private final Gson gson;

    private final StreamingServer streamingServer;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public CommandExecutor(StreamingServer streamingServer, SongRepository songRepository,
                           UserRepository userRepository, PlaylistRepository playlistRepository) {
        this.streamingServer = streamingServer;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.gson = new Gson();
    }

    public String execute(Command command, String clientId) {
        return switch (command.command()) {
            case REGISTER -> handleRegister(command.arguments());
            case LOGIN -> handleLogin(command.arguments(), clientId);
            case DISCONNECT -> handleDisconnect(command.arguments(), clientId);
            case SEARCH -> handleSearch(command.arguments(), clientId);
            case TOP_LISTENED -> handleTopListened(command.arguments(), clientId);
            case CREATE_PLAYLIST -> handleCreatePlaylist(command.arguments(), clientId);
            case ADD_SONG_TO -> handleAddSongTo(command.arguments(), clientId);
            case SHOW_PLAYLIST -> handleShowPlaylist(command.arguments(), clientId);
            case PLAY -> handlePlay(command.arguments(), clientId);
            case STOP -> handleStop(command.arguments(), clientId);
            default -> "Unknown command";
        };
    }

    private String handleRegister(String[] args) {
        if (args.length != REGISTER_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, REGISTER, REGISTER_ARG_COUNT,
                REGISTER + " <email> <password>");
            return gson.toJson(ServerResponse.message(response));
        }

        String email = args[EMAIL_ARG_IDX];
        String password = args[PASSWORD_ARG_IDX];

        if (!EmailValidator.isValid(email)) {
            return gson.toJson(ServerResponse.message("Invalid email format. Please use a valid email address."));
        }

        if (userRepository.register(email, password)) {
            return gson.toJson(ServerResponse.message("Successful registration!"));
        }

        return gson.toJson(ServerResponse.message("User with that email already exists!"));
    }

    private String handleLogin(String[] args, String clientId) {
        if (args.length != LOGIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, LOGIN, LOGIN_ARG_COUNT,
                LOGIN + " <email> <password>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("Already logged in. Disconnect first."));
        }

        String email = args[EMAIL_ARG_IDX];
        String password = args[PASSWORD_ARG_IDX];

        if (userRepository.login(email, password, clientId)) {
            return gson.toJson(ServerResponse.message("Successful login! Welcome to Spotify!"));
        }

        return gson.toJson(ServerResponse.message("Invalid email or password."));
    }

    private String handleDisconnect(String[] args, String clientId) {
        if (args.length != DISCONNECT_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, DISCONNECT, DISCONNECT_ARG_COUNT, DISCONNECT);
            return gson.toJson(ServerResponse.message(response));
        }

        if (userRepository.logout(clientId)) {
            return gson.toJson(ServerResponse.message("Disconnected successfully."));
        }

        return gson.toJson(ServerResponse.message("You are not logged in."));
    }

    private String handleSearch(String[] args, String clientId) {
        if (args.length < SEARCH_MIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, SEARCH, SEARCH_MIN_ARG_COUNT, SEARCH + " <words>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        String query = String.join(" ", args);
        List<Song> results = songRepository.search(query);

        if (results.isEmpty()) {
            return gson.toJson(ServerResponse.message("No songs found matching: " + query));
        }

        StringBuilder sb = new StringBuilder("Search results:\n");
        for (int i = 0; i < results.size(); i++) {
            Song song = results.get(i);
            sb.append(String.format("%d. %s\n", i + 1, song.displayName()));
        }

        return gson.toJson(ServerResponse.message(sb.toString().trim()));
    }

    private String handleTopListened(String[] args, String clientId) {
        if (args.length != TOP_LISTENED_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, TOP_LISTENED, TOP_LISTENED_ARG_COUNT,
                TOP_LISTENED + " <number>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        int count;
        try {
            count = Integer.parseInt(args[TOP_NUMBER_ARG_IDX]);
        } catch (NumberFormatException e) {
            return gson.toJson(ServerResponse.message(args[TOP_NUMBER_ARG_IDX] + " is not a valid number."));
        }
        if (count <= 0) {
            return gson.toJson(ServerResponse.message("Number must be positive"));
        }

        List<Song> topSongs = songRepository.getTopSongs(count);
        if (topSongs.isEmpty()) {
            return gson.toJson(ServerResponse.message("No songs have been played yet."));
        }

        StringBuilder sb = new StringBuilder("Top " + topSongs.size() + " most played songs:\n");
        for (int i = 0; i < topSongs.size(); i++) {
            Song song = topSongs.get(i);
            sb.append(String.format("%d. %s (%d plays)\n", i + 1, song.displayName(), song.playCount()));
        }

        return gson.toJson(ServerResponse.message(sb.toString().trim()));
    }

    private String handleCreatePlaylist(String[] args, String clientId) {
        if (args.length < CREATE_PLAYLIST_MIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, CREATE_PLAYLIST, CREATE_PLAYLIST_MIN_ARG_COUNT,
                CREATE_PLAYLIST + " <name_of_playlist>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        String name = String.join(" ", args);
        String owner = userRepository.getEmailByClientId(clientId);

        if (playlistRepository.createPlaylist(name, owner)) {
            return gson.toJson(ServerResponse.message("Playlist \"" + name + "\" created successfully!"));
        }

        return gson.toJson(ServerResponse.message("Playlist with this name already exists."));
    }

    private String handleAddSongTo(String[] args, String clientId) {
        if (args.length < ADD_SONG_TO_MIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, ADD_SONG_TO, ADD_SONG_TO_MIN_ARG_COUNT,
                ADD_SONG_TO + " <name_of_playlist> <song>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        String playlistName = args[PLAYLIST_NAME_ARG_IDX];
        String songQuery = String.join(" ", Arrays.copyOfRange(args, PLAYLIST_NAME_ARG_IDX + 1, args.length));
        String owner = userRepository.getEmailByClientId(clientId);

        String result = playlistRepository.addSongToPlaylist(playlistName, songQuery, owner, songRepository);
        return gson.toJson(ServerResponse.message(result));
    }

    private String handleShowPlaylist(String[] args, String clientId) {
        if (args.length < SHOW_PLAYLIST_MIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, SHOW_PLAYLIST, SHOW_PLAYLIST_MIN_ARG_COUNT,
                SHOW_PLAYLIST + " <name_of_playlist>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        String name = String.join(" ", args);
        String owner = userRepository.getEmailByClientId(clientId);

        String result = playlistRepository.showPlaylist(name, owner, songRepository);
        return gson.toJson(ServerResponse.message(result));
    }

    private String handlePlay(String[] args, String clientId) {
        if (args.length < PLAY_MIN_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, PLAY, PLAY_MIN_ARG_COUNT, PLAY + " <song>");
            return gson.toJson(ServerResponse.message(response));
        }

        if (!userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("You must be logged in to do that"));
        }

        String songName = String.join(" ", args);
        Optional<Song> songOpt = songRepository.findSong(songName);

        if (songOpt.isEmpty()) {
            return gson.toJson(ServerResponse.message("Song not found: " + songName));
        }

        Song song = songOpt.get();
        if (!songRepository.songFileExists(song)) {
            return gson.toJson(ServerResponse.message("Song \"" + song.displayName() + "\" is currently unavailable."));
        }

        return playSong(song, clientId);
    }

    private String playSong(Song song, String clientId) {
        File songFile = songRepository.getSongPath(song).toFile();
        songRepository.incrementPlayCount(song);

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(songFile)) {
            AudioFormat format = audioStream.getFormat();
            streamingServer.registerStream(clientId, songFile.toPath());

            return handleStreamResponse(format, clientId, song.displayName());
        } catch (UnsupportedAudioFileException | IOException e) {
            Logger.logError("Failed to play song: " + song.displayName(), e,
                userRepository.getEmailByClientId(clientId));
            return gson.toJson(ServerResponse.message("Unable to play song. Please try again later."));
        }
    }

    private String handleStop(String[] args, String clientId) {
        if (args.length != STOP_ARG_COUNT) {
            String response = String.format(INVALID_FORMAT_MESSAGE, STOP, STOP_ARG_COUNT, STOP);
            return gson.toJson(ServerResponse.message(response));
        }

        if (userRepository.isLoggedIn(clientId)) {
            return gson.toJson(ServerResponse.message("Already logged in. Disconnect first."));
        }

        streamingServer.unregisterStream(clientId);
        return gson.toJson(ServerResponse.stopAck());
    }

    private String handleStreamResponse(AudioFormat format, String clientId, String song) {
        StreamReadyResponse streamResponse = new StreamReadyResponse(
            format.getEncoding().toString(),
            format.getSampleRate(),
            format.getSampleSizeInBits(),
            format.getChannels(),
            format.getFrameSize(),
            format.getFrameRate(),
            format.isBigEndian(),
            clientId,
            song
        );

        return gson.toJson(ServerResponse.streamReady(streamResponse));
    }
}
