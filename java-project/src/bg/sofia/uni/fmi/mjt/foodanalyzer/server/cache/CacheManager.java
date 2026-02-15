package bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.CacheException;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class CacheManager {
    private static final String CACHE_DIR = "cache";
    private static final String CACHE_DIR_FOOD = "cache/food";
    private static final String CACHE_DIR_BARCODE = "cache/barcodes";
    private static final String CACHE_DIR_FOOD_REPORT = "cache/report";

    private final Path cacheDir;
    private final Path foodDir;
    private final Path reportDir;
    private final Path barcodeDir;

    private static final CacheManager INSTANCE = new CacheManager();

    //Converts a path string, or a sequence of strings that when joined form a path string, to a Path.
    private CacheManager() {
        this.cacheDir = Paths.get(CACHE_DIR);
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
            //System.out.println("DEBUG: Creating folders at: " + foodDir.toAbsolutePath());
            Files.createDirectories(foodDir);
            Files.createDirectories(reportDir);
            Files.createDirectories(barcodeDir);
        } catch (IOException e) {
            throw new CacheException("Failed to initialise directories", e); // adni go v loggera za greshki
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

    //vuv failowata sistema sa code.txt Ako e img purvo dekodirame do barcode posle go vzimame i s nego tursim
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
            throw new CacheException("Failed to write to cache: " + filePath, e);
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
            throw new CacheException("Failed to read from cache: " + filePath);
        }
    }

    public void clearCache() {
        clearDir(foodDir);
        clearDir(reportDir);
        clearDir(barcodeDir);
    }

    private void clearDir(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

            for (Path fileInDir : stream) {
                Files.delete(fileInDir);
            }

        } catch (IOException | DirectoryIteratorException e) {
            throw new CacheException("Failed to clear cache");
        }
    }
}
