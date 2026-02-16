package bg.sofia.uni.fmi.mjt.foodanalyzer.server.command;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.FoodDataApiClient;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.AbridgedFoodNutrient;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.FoodDetailsResponse;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.cache.CacheManager;
import com.google.gson.Gson;

import java.util.Optional;

public class GetFoodReportCommand implements Command {

    private int foodFdcId;
    private static FoodDataApiClient apiClient = new FoodDataApiClient();
    private static CacheManager cacheManager = CacheManager.getInstance();
    private static final Gson GSON = new Gson();

    public GetFoodReportCommand(int foodFdcId) {
        this.foodFdcId = foodFdcId;
    }

    @Override
    public String execute() {
        try {
            Optional<String> cachedJson = cacheManager.getReport(foodFdcId);
            FoodDetailsResponse response;

            if (cachedJson.isPresent()) {
                response = GSON.fromJson(cachedJson.get(), FoodDetailsResponse.class);
                //cache to return the objects?
            } else {
                response = apiClient.getFoodReport(foodFdcId);
                String jsonToSave = GSON.toJson(response);
                cacheManager.cacheReport(foodFdcId, jsonToSave);
            }

            return formatSearchResultFood(response);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatSearchResultFood(FoodDetailsResponse response) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("FDC ID: %d\n", response.fdcId()));
        sb.append(String.format("Data Type: %s\n", response.dataType()));
        sb.append(String.format("Ingredients: %s\n", response.ingredients()));

        for (AbridgedFoodNutrient nutrient : response.foodNutrients()) {
            addNutrientIfPresent(sb, nutrient);
        }

        return sb.toString().trim();
    }

    private void addNutrientIfPresent(StringBuilder sb, AbridgedFoodNutrient nutrient) {
        if (nutrient.nutrientName() != null) {
            sb.append(String.format("Nutrient ID: %d\n", nutrient.nutrientId()));
            sb.append(String.format("Nutrient Name: %s\n", nutrient.nutrientName()));
            sb.append(String.format("Unit Name: %s\n", nutrient.unitName()));
            sb.append(String.format("Value: %s\n", nutrient.value()));
            sb.append("\n");
        }
    }
}
