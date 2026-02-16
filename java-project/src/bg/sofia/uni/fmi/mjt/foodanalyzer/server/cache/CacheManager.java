package bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.CacheException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class CacheManager {
    private static final String CACHE_DIR_FOOD = "cache/food";
    private static final String CACHE_DIR_BARCODE = "cache/barcodes";
    private static final String CACHE_DIR_FOOD_REPORT = "cache/report";

    private final Path foodDir;
    private final Path reportDir;
    private final Path barcodeDir;

    private static final CacheManager INSTANCE = new CacheManager();

    private CacheManager() {
        this.foodDir = Paths.get(CACHE_DIR_FOOD);
        this.reportDir = Paths.get(CACHE_DIR_FOOD_REPORT);
        this.barcodeDir = Paths.get(CACHE_DIR_BARCODE);

        initDirectories();
    }

    public static CacheManager getInstance() {
        return INSTANCE;
    }

    private void initDirectories() {
        try {
            Files.createDirectories(foodDir);
            Files.createDirectories(reportDir);
            Files.createDirectories(barcodeDir);
        } catch (IOException e) {
            throw new CacheException("Failed to initialise cache directories", e);
        }
    }

    public synchronized void cacheReport(int fdcId, String jsonData) throws CacheException {
        Path filePath = reportDir.resolve(fdcId + ".json");
        writeToCache(filePath, jsonData);
    }

    public synchronized Optional<String> getReport(int fdcId) {
        Path filePath = reportDir.resolve(fdcId + ".json");
        return readFromCache(filePath);
    }

    public synchronized void cacheBarcode(String barcode, int fdcId) throws CacheException {
        Path filePath = barcodeDir.resolve(barcode + ".txt");
        writeToCache(filePath, String.valueOf(fdcId));
    }

    public synchronized Optional<Integer> getFdcIdByBarcode(String barcode) throws CacheException {
        Path filePath = barcodeDir.resolve(barcode + ".txt");
        Optional<String> fileContent = readFromCache(filePath);

        return fileContent.map(s -> Integer.parseInt(s.trim()));
    }

    public synchronized void cacheFood(String foodName, String jsonData) throws CacheException {
        Path filePath = foodDir.resolve(foodName + ".json");
        writeToCache(filePath, jsonData);
    }

    public synchronized Optional<String> getFood(String foodName) throws CacheException {
        Path filePath = foodDir.resolve(foodName + ".json");
        return readFromCache(filePath);
    }

    private synchronized void writeToCache(Path filePath, String jsonData) throws CacheException {
        try {
            Files.writeString(filePath, jsonData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new CacheException("Failed to write to cache: " + filePath, e); //tova e smurtna greshka
        }
    }

    private synchronized Optional<String> readFromCache(Path filePath) {
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try {
            String json = Files.readString(filePath);
            return Optional.of(json);
        } catch (IOException e) {
            Logger.logError("Failed to read from cache: " + filePath, e); //tova e nesh s koeto moga da jiveq
            return Optional.empty();
        }
    }
}
