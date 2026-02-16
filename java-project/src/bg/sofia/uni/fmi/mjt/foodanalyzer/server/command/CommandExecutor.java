package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;

import java.io.IOException;

public class CommandExecutor {
    private final CommandFactory commandFactory = new CommandFactory();

    public CommandExecutor() {

    }

    public String execute(String commandString) throws InvalidCommandException, IOException {
        try {
            Command command = commandFactory.createCommand(commandString);
            return command.execute();
        } catch (InvalidCommandException e) {
            Logger.logError("Invalid command: " + e.getMessage(), e);
            //do i need to log invalid commands dont do stuff
            return e.getMessage();
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e); //failed decoding
            return e.getMessage();
        }

    }
}
