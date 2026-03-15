package bg.sofia.uni.fmi.mjt.music.audio;

import bg.sofia.uni.fmi.mjt.music.error.ErrorHandler;
import bg.sofia.uni.fmi.mjt.music.error.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlayer {

    private static final int BUFFER_SIZE = 4096;

    private SourceDataLine dataLine;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Socket streamSocket;

    public void play(String host, int port, String clientId, AudioFormat format)
        throws IOException, LineUnavailableException {
        streamSocket = new Socket(host, port);

        streamSocket.getOutputStream().write((clientId + "\n").getBytes());
        streamSocket.getOutputStream().flush();

        initializeAudioLine(format);

        Thread playbackThread = new Thread(() -> {
            try (InputStream in = streamSocket.getInputStream()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while (isPlaying.get() && (bytesRead = in.read(buffer)) != -1) {
                    dataLine.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                if (isPlaying.get()) {
                    Logger.logError("Playback error", e, null);
                    System.out.println(ErrorHandler.getUserMessage(e));
                }
            } finally {
                stop();
            }
        });

        playbackThread.start();
    }

    public void stop() {
        isPlaying.set(false);
        closeSocket();
        closeDataLine();
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    private void initializeAudioLine(AudioFormat format) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        dataLine = (SourceDataLine) AudioSystem.getLine(info);
        dataLine.open(format);
        dataLine.start();
        isPlaying.set(true);
    }

    private void closeSocket() {
        try {
            if (streamSocket != null && !streamSocket.isClosed()) {
                streamSocket.close();
            }
        } catch (IOException e) {
            Logger.logError("Error occurred while closing socket", e, null);
            System.out.println(ErrorHandler.getUserMessage(e));
        }
    }

    private void closeDataLine() {
        if (dataLine != null) {
            dataLine.drain();
            dataLine.stop();
            dataLine.close();
        }
    }
}
