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
        validateStringCommand(commandString);

        String[] commandParts = commandString.trim().split("\\s+", 2);
        String cmd = commandParts[0].toLowerCase();

        switch (cmd) {
            case CMD_GET_FOOD:
                if (isValidCommand(commandParts)) {
                    return new GetFoodCommand(commandParts[1]);
                }

                throw new InvalidCommandException("Missing food name! Command usage get-food <food_name>");
            case CMD_GET_FOOD_REPORT:
                if (isValidCommand(commandParts)) {
                    if (isValidFdcId(commandParts[1])) {
                        return new GetFoodReportCommand(Integer.parseInt(commandParts[1].trim()));
                    }

                    throw new InvalidCommandException(
                        "FDC ID must be positive! Command usage get-food-report positive <fdc_id>");
                }

                throw new InvalidCommandException("Missing FDC ID! Command usage get-food-report positive <fdc_id>");
            case CMD_GET_FOOD_BY_BARCODE:
                if (isValidCommand(commandParts)) {
                    return new GetFoodByBarcodeCommand(extractBarcode(commandParts[1].trim()));
                }
                throw new InvalidCommandException(
                    "Invalid barcode command. Must specify --code=<barcode> or --img=<image path>!");
            default:
                throw new InvalidCommandException("Unknown command!");
        }
    }

    private void validateStringCommand(String commandString) throws InvalidCommandException {
        if (commandString == null || commandString.trim().isBlank()) {
            throw new InvalidCommandException("Command cannot be null or blank!");
        }
    }

    private boolean isValidCommand(String[] commandParts) {
        return commandParts.length >= 2 && !commandParts[1].isBlank();
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
                throw new InvalidCommandException("Failed to decode barcode from image: " + e.getMessage());
            }
        }

        throw new InvalidCommandException(
            "Invalid barcode command. Must specify --code=<barcode> or --img=<image path>!");
    }
}
