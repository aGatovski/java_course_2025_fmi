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
    private static final Pattern CODE_PATTERN = Pattern.compile("--code=([^\\s]+)");
    private static final Pattern IMG_PATTERN = Pattern.compile("--img=([^\\s]+)");

    public CommandFactory() {

    }

    public Command createCommand(String commandString) throws InvalidCommandException, IOException {
        validateStringCommand(commandString);

        String[] commandParts = commandString.trim().split("\\s+", 2);
        String cmd = commandParts[0].toLowerCase();
        String args = commandParts.length > 1 ? commandParts[1].trim() : "";

        return switch (cmd) {
            case CMD_GET_FOOD -> createGetFood(args);
            case CMD_GET_FOOD_REPORT -> createGetFoodReport(args);
            case CMD_GET_FOOD_BY_BARCODE -> createBarcodeCommand(args);
            default -> throw new InvalidCommandException("Unknown command!");
        };
    }

    private void validateStringCommand(String commandString) throws InvalidCommandException {
        if (commandString == null || commandString.trim().isBlank()) {
            throw new InvalidCommandException("Command cannot be null or blank!");
        }
    }

    private Command createGetFood(String args) throws InvalidCommandException {
        if (args.isBlank()) {
            throw new InvalidCommandException("Missing food name! Command usage get-food <food_name>");
        }
        return new GetFoodCommand(args);
    }

    private Command createGetFoodReport(String args) throws InvalidCommandException {
        if (args.isBlank()) {
            throw new InvalidCommandException("Missing FDC ID! Usage: get-food-report <fdc_id>");
        }
        try {
            int fdcId = Integer.parseInt(args.trim());
            if (fdcId <= 0) {
                throw new NumberFormatException();
            }
            return new GetFoodReportCommand(fdcId);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("FDC ID must be positive! Usage: get-food-report <fdc_id>");
        }
    }

    private Command createBarcodeCommand(String args) throws InvalidCommandException, IOException {
        if (args.isBlank()) {
            throw new InvalidCommandException("Invalid barcode command. Specify --code=<val> or --img=<path>");
        }
        return new GetFoodByBarcodeCommand(extractBarcode(args));
    }

    private String extractBarcode(String barcodeString) throws InvalidCommandException {
        Matcher codeMatcher = CODE_PATTERN.matcher(barcodeString);

        if (codeMatcher.find()) {
            return codeMatcher.group(1);
        }

        Matcher imgMatcher = IMG_PATTERN.matcher(barcodeString);

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
