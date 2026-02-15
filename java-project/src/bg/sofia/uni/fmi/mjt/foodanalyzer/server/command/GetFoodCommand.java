package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.FoodDataApiClient;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.SearchResult;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.SearchResultFood;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache.CacheManager;
import com.google.gson.Gson;

import java.util.Optional;

public class GetFoodCommand implements Command {

    private String foodName;
    private static FoodDataApiClient apiClient = new FoodDataApiClient();
    private static CacheManager cacheManager = CacheManager.getInstance();
    private static final Gson GSON = new Gson();

    public GetFoodCommand(String foodName) {
        this.foodName = foodName;
    }

    @Override
    public String execute() {
        try {
            Optional<String> cachedJson = cacheManager.getFood(foodName);
            SearchResult response;

            if (cachedJson.isPresent()) {
                response = GSON.fromJson(cachedJson.get(), SearchResult.class);
            } else {
                response = apiClient.getFood(foodName);
                String jsonToSave = GSON.toJson(response);
                cacheManager.cacheFood(foodName, jsonToSave);
            }

            return formatSearchResult(response);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatSearchResult(SearchResult response) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d results:\n", response.totalHits()));

        for (SearchResultFood food : response.foods()) {
            sb.append(String.format("FDC ID: %d\n", food.fdcId()));
            sb.append(String.format("Description: %s\n", food.description()));

            if (food.gtinUpc() != null && !food.gtinUpc().isEmpty()) {
                sb.append(String.format("Barcode: %s\n", food.gtinUpc()));
            }

            if (food.brandOwner() != null && !food.brandOwner().isEmpty()) {
                sb.append(String.format("Brand: %s\n", food.brandOwner()));
            }

            if (food.ingredients() != null && !food.ingredients().isEmpty()) {
                sb.append(String.format("Ingredients: %s\n", food.ingredients()));
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }
}
