package bg.sofia.uni.fmi.mjt.music.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final Path LOG_FILE = Path.of("logs", "error-logs.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logError(String message, Throwable exception, String clientEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(LocalDateTime.now().format(FORMATTER)).append("] ");
        sb.append("ERROR: ").append(message).append("\n");

        if (clientEmail != null) {
            sb.append("Client: ").append(clientEmail).append("\n");
        }

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw).append("\n");

        writeToLogFile(sb.toString());
    }

    public static Path getLogPath() {
        return LOG_FILE.toAbsolutePath();
    }

    private static void writeToLogFile(String content) {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            try (var bufferedWriter = Files.newBufferedWriter(LOG_FILE, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
                bufferedWriter.write(content);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
