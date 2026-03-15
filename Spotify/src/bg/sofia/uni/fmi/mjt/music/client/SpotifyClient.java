package bg.sofia.uni.fmi.mjt.music.client;

import bg.sofia.uni.fmi.mjt.music.audio.AudioPlayer;
import bg.sofia.uni.fmi.mjt.music.error.ErrorHandler;
import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.model.ServerResponse;
import bg.sofia.uni.fmi.mjt.music.model.StreamReadyResponse;
import com.google.gson.Gson;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SpotifyClient {

    private static final int SERVER_PORT = 7777;
    private static final int STREAMING_PORT = 7778;
    private static final String HOSTNAME = "172.20.10.9";
    private static final String ENCODING = "UTF-8";

    private static AudioPlayer audioPlayer;
    private static final Gson GSON = new Gson();

    static void main() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, ENCODING));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, ENCODING), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(HOSTNAME, SERVER_PORT));

            System.out.println("Connected to Spotify Server!");
            printAvailableCommands();

            runClientLoop(reader, writer, scanner);

        } catch (IOException e) {
            Logger.logError("Client connection failed", e, null);
            System.out.println(ErrorHandler.getUserMessage(e));
        }
    }

    private static void runClientLoop(BufferedReader reader, PrintWriter writer, Scanner scanner) throws IOException {
        while (true) {
            System.out.print("> ");
            String message = scanner.nextLine();

            if (message == null || message.isBlank()) {
                continue;
            }

            writer.println(message);

            String reply = reader.readLine();
            if (reply == null) {
                System.out.println("Server closed connection.");
                break;
            }

            ServerResponse response = GSON.fromJson(reply, ServerResponse.class);

            switch (response.type()) {
                case STREAM_READY -> handleStreamReady(response.streamInfo());
                case STOP_ACK -> handleStreamingStop();
                case MESSAGE -> System.out.println(response.message());
            }
        }
    }

    private static void handleStreamingStop() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stop();
            System.out.println("Playback stopped.");
        } else {
            System.out.println("Nothing is playing. Cannot be stopped.");
        }
    }

    private static void handleStreamReady(StreamReadyResponse info) {
        // stop existing player
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stop();
        }

        try {
            AudioFormat.Encoding encoding = new AudioFormat.Encoding(info.encoding());
            AudioFormat format = new AudioFormat(encoding, info.sampleRate(), info.sampleSizeInBits(),
                info.channels(), info.frameSize(), info.frameRate(), info.bigEndian());

            audioPlayer = new AudioPlayer();
            audioPlayer.play(HOSTNAME, STREAMING_PORT, info.clientId(), format);

            System.out.println("Now playing: " + info.song());

        } catch (IOException | LineUnavailableException e) {
            Logger.logError("Failed to start playback", e, null);
            System.out.println(ErrorHandler.getUserMessage(e));
        }
    }

    private static void printAvailableCommands() {
        System.out.println("Available commands:");
        System.out.println("- register <email> <password>");
        System.out.println("- login <email> <password>");
        System.out.println("- disconnect");
        System.out.println("- search <words>");
        System.out.println("- top <number>");
        System.out.println("- create-playlist <name_of_playlist>");
        System.out.println("- add-song-to <name_of_playlist> <song>");
        System.out.println("- show-playlist <name_of_playlist>");
        System.out.println("- play <song>");
        System.out.println("- stop");
        System.out.println();
    }
}
