package bg.sofia.uni.fmi.mjt.music.error;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class ErrorHandler {

    public static String getUserMessage(Throwable exception) {
        return switch (exception) {
            case IOException ignored -> "A network error occurred. Please check your connection and try again. " +
                "You could also contact an administrator by providing the logs in " + Logger.getLogPath();
            case LineUnavailableException ignored -> "Audio device is unavailable. Please check your audio settings.";
            case UnsupportedAudioFileException ignored -> "The audio file format is not supported.";
            default -> "An unexpected error occurred. Please try again later. Alternatively contact an administrator " +
                "and provide the logs from " + Logger.getLogPath();
        };
    }
}
