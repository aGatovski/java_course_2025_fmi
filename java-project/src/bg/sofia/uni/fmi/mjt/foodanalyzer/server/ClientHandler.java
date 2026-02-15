package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.server.command.CommandExecutor;

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
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             socket) {

            String command;
            command = in.readLine(); // read the message from the client

            String response = commandExecutor.execute(command); // commandExecutor kazva koq komanda

            out.println(response); // send response back to the client/Server

        } catch (IOException e) {
            throw new RuntimeException(e); //logger
        }
    }
}
