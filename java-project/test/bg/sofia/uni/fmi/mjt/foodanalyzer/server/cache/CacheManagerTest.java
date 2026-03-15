package bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheManagerTest {
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = CacheManager.getInstance();
    }

    @Test
    void testGetInstanceReturnSameInstance() {
        CacheManager cacheManager1 = CacheManager.getInstance();
        CacheManager cacheManager2 = CacheManager.getInstance();

        assertSame(cacheManager1, cacheManager2, "getInstance() method should return same instance!");
    }

    @Test
    void testGetFoodNotInCache() {
        Optional<String> food = cacheManager.getFood("no food");
        assertTrue(food.isEmpty(), "Should return empty Optional when food is not cached!");
    }

    @Test
    void testGetFoodReportNotInCache() {
        int magicNumber = 999999999;
        Optional<String> foodReport = cacheManager.getReport(magicNumber);
        assertTrue(foodReport.isEmpty(), "Should return empty Optional when food report is not cached!");
    }

    @Test
    void testGetFoodBarcodeNotInCache() {
        String nonCachedBarcode = "NonCachedBarcode";
        Optional<Integer> fdcIdByBarcode = cacheManager.getFdcIdByBarcode(nonCachedBarcode);
        assertTrue(fdcIdByBarcode.isEmpty(), "Should return empty Optional when food barcode is not cached!");
    }
}