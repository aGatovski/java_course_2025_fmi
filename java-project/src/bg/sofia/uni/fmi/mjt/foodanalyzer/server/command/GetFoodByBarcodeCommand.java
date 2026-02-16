package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache.CacheManager;

import java.util.Optional;

public class GetFoodByBarcodeCommand implements Command {
    private String barcode;
    private static CacheManager cacheManager = CacheManager.getInstance();

    public GetFoodByBarcodeCommand(String barcode) {

        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException("Barcode cannot be null!");
        }
        this.barcode = barcode;
    }

    @Override
    public String execute() {
        Optional<Integer> fdcID = cacheManager.getFdcIdByBarcode(barcode);

        if (fdcID.isEmpty()) {
            return "Barcode not found in cache.";
        }

        GetFoodReportCommand foodReportCommand = new GetFoodReportCommand(fdcID.get());

        return foodReportCommand.execute();
    }
}
