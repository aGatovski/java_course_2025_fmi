package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {

    private static final String CMD_GET_FOOD = "get-food";
    private static final String CMD_GET_FOOD_REPORT = "get-food-report";
    private static final String CMD_GET_FOOD_BY_BARCODE = "get-food=by-barcode";
    private static final String CMD_QUIT = "quit";

    public CommandFactory() {

    }

    public Command createCommand(String commandString) throws InvalidCommandException {
        if (commandString == null || commandString.trim().isBlank()) { // mislq che trqbwa da izmislq drug handle
            throw new IllegalArgumentException("Command string cannot be null or blank!"); // ne iskam da prikluchva tuk
        }

        String[] commandParts = commandString.trim().split("\\s+", 2);
        String cmd = commandParts[0].toLowerCase();

        if (commandParts.length < 2) {
            throw new InvalidCommandException("Missing command arguments");
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
                return new GetFoodByBarcodeCommand(extractBarcode(commandParts[1].trim())) ;
            default:
                throw new InvalidCommandException("Unknown command");
        }
    }

    /*
     * Validate FDC ID is integer
     * */
    private boolean isValidFdcId(String fdcIdString) {
        try {
            int fdcId = Integer.parseInt(fdcIdString.trim());
            return fdcId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String extractBarcode(String barcodeString) {

        Pattern codePattern = Pattern.compile("--code=([^\\s]+)");
        Matcher codeMatcher = codePattern.matcher(barcodeString);

        if (codeMatcher.find()) {
            System.out.println("Extracted Code: " + codeMatcher.group(1));
            return codeMatcher.group(1);
        }

        Pattern imgPattern = Pattern.compile("--img=([^\\s]+)");
        Matcher imgMatcher = imgPattern.matcher(barcodeString);

        if (imgMatcher.find()) {
            return imgMatcher.group(1);
        }

        //throw new InvalidCommandException("Barcode command is wrong");
        return null; //za sq taka posle mai shte e po dobre exeption handle
    }
}
