package bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final Path LOG_DIR = Path.of("logs");
    private static final Path LOG_ERR_FILE = Path.of("logs", "error_file.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
        }
    }

    public static void logError(String message, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println(formatMessage(message));

        if (e != null) {
            e.printStackTrace(pw);
        }

        String fullReport = sw.toString();

        System.out.print(e.getMessage());
        writeToFile(fullReport);
    }

    private static String formatMessage(String message) {
        String timeStamp = LocalDateTime.now().format(FORMATTER);
        return String.format("%s: %s", timeStamp, message);
    }

    private static synchronized void writeToFile(String message) {
        try {
            Files.writeString(LOG_ERR_FILE, message + "\n" , StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException E) {
            System.err.println("Failed to write to log file: " + message);
        }
    }
}
