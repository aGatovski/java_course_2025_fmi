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
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null!");
        }
        this.socket = socket;
        this.commandExecutor = new CommandExecutor();
    }

    @Override
    public void run() {
        String clientAddress = socket.getRemoteSocketAddress().toString();

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             this.socket) {

            String command;
            while ((command = in.readLine()) != null) {
                if (command.isBlank()) {
                    continue;
                }

                if (command.equalsIgnoreCase("quit")) {
                    out.println("END_RESPONSE");
                    break;
                }

                processCommand(command, out, clientAddress);
                out.println("END_RESPONSE");
            }
        } catch (IOException e) {
            Logger.logError("Error handling client: " + clientAddress, e);
        } catch (Exception e) {
            Logger.logError("Unexpected error handling client " + clientAddress, e);
        }
    }

    private void processCommand(String command, PrintWriter out, String clientAddress) {
        try {
            String response = commandExecutor.execute(command);
            out.println(response);
        } catch (InvalidCommandException e) {
            out.println("Error: " + e.getMessage());
            Logger.logError("Invalid command: " + e.getMessage(), e);
        } catch (Exception e) {
            Logger.logError("Error executing command from " + clientAddress, e);
            out.println("Unexpected error occurred. Check error logs!");
        }
    }
}

