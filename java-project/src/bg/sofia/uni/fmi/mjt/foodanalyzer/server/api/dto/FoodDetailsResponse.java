package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto;

import java.util.List;

public record FoodDetailsResponse(int fdcId, String description, String dataType, String ingredients,
                                  List<AbridgedFoodNutrient> foodNutrients) {
}
