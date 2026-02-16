package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodAnalyzerServer {
    private static final int MAX_EXECUTOR_THREADS = 10;
    private static final int SERVER_PORT = 8080;

    public boolean isServerRunning;

    public FoodAnalyzerServer() {
        this.isServerRunning = false;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executorService = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS)) {

            this.isServerRunning = true;

            System.out.println("Server started and listening for connection requests on port " + SERVER_PORT);

            Socket clientSocket;

            while (isServerRunning) {
                clientSocket = serverSocket.accept();

                System.out.println(
                    "Accepted connection request from client " + clientSocket.getInetAddress() + ":" +
                        clientSocket.getPort());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            Logger.logError("Failed to start server", e);
        }
    }

    static void main() {
        FoodAnalyzerServer server = new FoodAnalyzerServer();
        server.start();
    }
}
