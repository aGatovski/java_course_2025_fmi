package bg.sofia.uni.fmi.mjt.foodanalyzer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class FoodAnalyzerClient {
    private static final String CMD_QUIT = "quit";
    private static final int SERVER_PORT = 8080;

    private static String handleResponse(BufferedReader reader) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equalsIgnoreCase("END_RESPONSE")) {
                break;
            }
            response.append(line).append("\n");
        }

        return response.toString();
    }

    static void main() {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // autoflush on
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("> ");
                String clientCommandMessage = scanner.nextLine().trim();

                if (clientCommandMessage.isBlank()) {
                    continue;
                }

                if (clientCommandMessage.equalsIgnoreCase(CMD_QUIT)) {
                    break;
                }

                writer.println(clientCommandMessage);

                String response = handleResponse(reader);
                System.out.println(response);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication. Check error_logs!", e);
        }
    }
}
