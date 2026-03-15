package bg.sofia.uni.fmi.mjt.music.server;

import bg.sofia.uni.fmi.mjt.music.error.Logger;
import bg.sofia.uni.fmi.mjt.music.server.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.music.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.music.server.repository.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.PlaylistRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.SongRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.music.server.streaming.StreamingServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class SpotifyServer {

    private static final String SERVER_HOST = "0.0.0.0";
    private static final int BUFFER_SIZE = 1024;

    private final int port;
    private final CommandExecutor commandExecutor;
    private final SongRepository songRepository;
    private final StreamingServer streamingServer;

    private boolean isServerRunning;
    private Selector selector;

    public SpotifyServer(int port, CommandExecutor commandExecutor,
                         SongRepository songRepository, StreamingServer streamingServer) {
        this.port = port;
        this.commandExecutor = commandExecutor;
        this.songRepository = songRepository;
        this.streamingServer = streamingServer;
    }

    public void start() {
        registerShutdownHook();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open()) {

            this.selector = selector;
            configureServerSocketChannel(serverSocketChannel, selector);

            System.out.println("Spotify Server started on port " + port);

            this.isServerRunning = true;

            runEventLoop();

        } catch (IOException e) {
            Logger.logError("Failed to start server", e, null);
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        } finally {
            closeResources();
        }
    }

    public void stop() {
        this.isServerRunning = false;

        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }

        System.out.println("Spotify Server stopped");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            saveData();
            stopServices();
        }));
    }

    private void saveData() {
        try {
            songRepository.save();
            System.out.println("Song data saved successfully");
        } catch (Exception e) {
            Logger.logError("Error saving song data", e, null);
            System.err.println("Error saving song data: " + e.getMessage());
        }
    }

    private void stopServices() {
        if (streamingServer != null) {
            streamingServer.stop();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(SERVER_HOST, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void runEventLoop() {
        while (isServerRunning) {
            try {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                processSelectedKeys();
            } catch (IOException e) {
                Logger.logError("Error occurred while processing client requests", e, null);
                System.err.println("Error occurred while processing client requests: " + e.getMessage());
            }
        }
    }

    private void processSelectedKeys() throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            keyIterator.remove();

            if (!key.isValid()) {
                continue;
            }

            if (key.isAcceptable()) {
                accept(selector, key);
            } else if (key.isReadable()) {
                handleRead(key);
            }
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel remoteClientChannel = serverChannel.accept();

        if (remoteClientChannel == null) {
            return;
        }

        remoteClientChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        remoteClientChannel.register(selector, SelectionKey.OP_READ, buffer);

        System.out.println("New client connected: " + remoteClientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            String clientId = clientChannel.getRemoteAddress().toString();
            System.out.println("Client disconnected: " + clientId);

            streamingServer.unregisterStream(clientId);

            clientChannel.close();
            key.cancel();
            return;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        String clientInput = new String(clientInputBytes, StandardCharsets.UTF_8);
        String clientId = clientChannel.getRemoteAddress().toString();

        String output = commandExecutor.execute(CommandCreator.newCommand(clientInput), clientId);
        writeClientOutput(clientChannel, output, buffer);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output, ByteBuffer buffer) throws IOException {
        buffer.clear();
        buffer.put((output + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void closeResources() {
        try {
            if (selector != null && selector.isOpen()) {
                for (SelectionKey key : selector.keys()) {
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        Logger.logError("Error closing channel", e, null);
                        System.err.println("Error closing channel: " + e.getMessage());
                    }
                }

                selector.close();
            }
        } catch (IOException e) {
            Logger.logError("Error closing selector (resources)", e, null);
            System.err.println("Error closing selector (resources): " + e.getMessage());
        }
    }

    static void main() {
        final int serverPort = 7777;

        StreamingServer streamingServer = new StreamingServer();
        streamingServer.start();

        SongRepository songRepository = new InMemorySongRepository();
        UserRepository userRepository = new InMemoryUserRepository();
        PlaylistRepository playlistRepository = new InMemoryPlaylistRepository();

        CommandExecutor commandExecutor = new CommandExecutor(streamingServer, songRepository,
            userRepository, playlistRepository);
        SpotifyServer server = new SpotifyServer(serverPort, commandExecutor, songRepository, streamingServer);
        server.start();
    }
}
