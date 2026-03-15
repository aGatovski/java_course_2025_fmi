package bg.sofia.uni.fmi.mjt.music.server.streaming;

import bg.sofia.uni.fmi.mjt.music.error.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class StreamingServer {

    private static final int STREAMING_PORT = 7778;
    private static final int BUFFER_SIZE = 4096;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Path> pendingStreams = new ConcurrentHashMap<>();
    private final Map<String, Socket> activeStreams = new ConcurrentHashMap<>();

    private ServerSocket serverSocket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void start() {
        isRunning.set(true);
        executor.submit(this::acceptConnections);
        System.out.println("Streaming server started on port " + STREAMING_PORT);
    }

    public void stop() {
        isRunning.set(false);

        for (Socket socket : activeStreams.values()) {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                Logger.logError("Error closing active stream", e, null);
            }
        }

        activeStreams.clear();
        pendingStreams.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.logError("Error occurred while closing socket", e, null);
            System.err.println("Error occurred while closing socket: " + e.getMessage());
        }

        executor.shutdownNow();
        System.out.println("Streaming server stopped");
    }

    public void registerStream(String clientId, Path songPath) {
        pendingStreams.put(clientId, songPath);
    }

    public void unregisterStream(String clientId) {
        pendingStreams.remove(clientId);
    }

    private void acceptConnections() {
        try {
            serverSocket = new ServerSocket(STREAMING_PORT);

            while (isRunning.get()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleStreamingClient(clientSocket));
            }
        } catch (IOException e) {
            if (isRunning.get()) {
                Logger.logError("Streaming server error", e, null);
                System.err.println("Streaming server error: " + e.getMessage());
            }
        }
    }

    private void handleStreamingClient(Socket clientSocket) {
        String clientId = null;
        try (clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            clientId = reader.readLine(); // client sends its identifier to get the song
            Path songPath = pendingStreams.remove(clientId);

            if (songPath == null) {
                System.err.println("No pending stream for client: " + clientId);
                return;
            }

            activeStreams.put(clientId, clientSocket);
            streamSong(songPath, out);

        } catch (IOException e) {
            Logger.logError("Error streaming to client", e, null);
            System.err.println("Error streaming to client: " + e.getMessage());
        } finally {
            if (clientId != null) {
                activeStreams.remove(clientId);
            }
        }
    }

    private void streamSong(Path songPath, OutputStream out) throws IOException {
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(songPath.toFile())) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (UnsupportedAudioFileException e) {
            Logger.logError("Unsupported audio format", e, null);
            System.err.println("Unsupported audio format: " + e.getMessage());
        }
    }
}
