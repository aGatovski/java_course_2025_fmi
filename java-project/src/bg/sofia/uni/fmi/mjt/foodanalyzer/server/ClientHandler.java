package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CommandExecutor commandExecutor;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        commandExecutor = new CommandExecutor();
    }

    @Override
    public void run() {
        String clientAddress = socket.getRemoteSocketAddress().toString();

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             socket) {

            String command;
            while ((command = in.readLine()) != null && !command.equalsIgnoreCase("quit")) {
                String response = "";
                try {
                    response = commandExecutor.execute(command);
                } catch (InvalidCommandException e) {
                    out.println("Invalid command!");
                    Logger.logError("Invalid command: " + e.getMessage(), e);
                }

                out.println(response);
                out.println("END_RESPONSE");
            }
        } catch (IOException e) {
            Logger.logError("Error handling client: " + clientAddress, e);
        }
    }
}
