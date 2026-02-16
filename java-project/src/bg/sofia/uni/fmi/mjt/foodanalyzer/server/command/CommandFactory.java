package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.util.BarcodeReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {

    private static final String CMD_GET_FOOD = "get-food";
    private static final String CMD_GET_FOOD_REPORT = "get-food-report";
    private static final String CMD_GET_FOOD_BY_BARCODE = "get-food-by-barcode";
    private static final String CMD_QUIT = "quit";

    public CommandFactory() {

    }

    public Command createCommand(String commandString) throws InvalidCommandException, IOException {
        if (commandString == null || commandString.trim().isBlank()) {
            throw new InvalidCommandException("Command cannot be null or blank!");
        }

        String[] commandParts = commandString.trim().split("\\s+", 2);
        String cmd = commandParts[0].toLowerCase();

        if (commandParts.length < 2) {
            throw new InvalidCommandException("Missing command arguments!");
        }

        switch (cmd) {
            case CMD_GET_FOOD:
                return new GetFoodCommand(commandParts[1]);
            case CMD_GET_FOOD_REPORT:
                if (!isValidFdcId(commandParts[1])) {
                    throw new InvalidCommandException("FCD ID must be a positive integer!");
                }

                return new GetFoodReportCommand(Integer.parseInt(commandParts[1].trim()));
            case CMD_GET_FOOD_BY_BARCODE:
                return new GetFoodByBarcodeCommand(extractBarcode(commandParts[1].trim()));
            default:
                throw new InvalidCommandException("Unknown command");
        }
    }

    private boolean isValidFdcId(String fdcIdString) {
        try {
            int fdcId = Integer.parseInt(fdcIdString.trim());
            return fdcId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String extractBarcode(String barcodeString) throws InvalidCommandException {
        Pattern codePattern = Pattern.compile("--code=([^\\s]+)");
        Matcher codeMatcher = codePattern.matcher(barcodeString);

        if (codeMatcher.find()) {
            return codeMatcher.group(1);
        }

        Pattern imgPattern = Pattern.compile("--img=([^\\s]+)");
        Matcher imgMatcher = imgPattern.matcher(barcodeString);

        if (imgMatcher.find()) {
            try {
                return BarcodeReader.decodeBarcode(imgMatcher.group(1));
            } catch (IOException e) {
                throw new InvalidCommandException("Failed to decode barcode from image!");
            }

        }

        throw new InvalidCommandException(
            "Invalid barcode command. Must specify --code=<barcode> or --img=<image path>!");
    }
}
