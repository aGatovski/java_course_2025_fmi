package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache.CacheManager;


public class Applicationfortest {
    public static void main(String[] args) throws ApiException {
        CacheManager cacheManager = CacheManager.getInstance();
        //int fdcId = 12345;
        String jsonData = "{\"name\": \"Apple\"}";

        // Act
        cacheManager.cacheFood("12345", jsonData);
        System.out.println(cacheManager.getFood("12345"));
    }
}
