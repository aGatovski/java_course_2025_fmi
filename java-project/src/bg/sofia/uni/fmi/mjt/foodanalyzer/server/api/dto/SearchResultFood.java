package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto;

import java.util.List;

public record SearchResultFood(int fdcId, String description, String dataType, String foodCode,
                               List<AbridgedFoodNutrient> foodNutrients, String publicationDate, String scientificName,
                               String brandOwner, String gtinUpc, String ingredients, int ndbNumber,
                               String additionalDescriptions, String allHighlightFields, Double score
) {

}
