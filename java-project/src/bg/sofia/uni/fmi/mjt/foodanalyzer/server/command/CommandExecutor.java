package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;

public class CommandExecutor {
    private final CommandFactory commandFactory = new CommandFactory();

    public CommandExecutor() {

    }

    public String execute(String commandString) {
        try {
            Command command = commandFactory.createCommand(commandString);

            return command.execute();
        } catch (InvalidCommandException e) {
            throw new RuntimeException(e);
        }
    }
}
