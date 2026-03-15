package bg.sofia.uni.fmi.mjt.foodanalyzer.client;

import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class FoodAnalyzerClient {
    private static final String CMD_QUIT = "quit";
    private static final String END_RESPONSE = "END_RESPONSE";
    private static final int SERVER_PORT = 8080;

    static void main() {
        try (SocketChannel socketChannel = SocketChannel.open();
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"));
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress("localhost", SERVER_PORT));

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("> ");
                String clientCommandMessage = scanner.nextLine().trim();

                if (CMD_QUIT.equalsIgnoreCase(clientCommandMessage)) {
                    break;
                }

                System.out.println("Sending message < " + clientCommandMessage +  "> to server");

                writer.println(clientCommandMessage);
                String response = handleResponse(reader);
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println(
                "There is a problem with the server communication. Check error_logs and try again in few minutes!");
            Logger.logError("There is a problem with the server communication.", e);
        }
    }

    private static String handleResponse(BufferedReader reader) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equalsIgnoreCase(END_RESPONSE)) {
                break;
            }
            response.append(line).append("\n");
        }

        return response.toString();
    }
}
