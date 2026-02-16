package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.FoodDetailsResponse;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.SearchResult;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.logger.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FoodDataApiClient {
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1";
    private static final String API_KEY = "umcMWkUywbqeSoZvilgc5aTVgI0xjceUZypaEVJV";
    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_SERVER_ERROR = 500;

    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;

    public FoodDataApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public SearchResult getFood(String foodName) throws ApiException {
        if (foodName == null || foodName.isBlank()) {
            throw new IllegalArgumentException("Food name cannot be null or blank!");
        }
        String encodedQuery = URLEncoder.encode(foodName, StandardCharsets.UTF_8);

        String url =
            String.format("%s/foods/search?query=%s&requireAllWords=true&api_key=%s", BASE_URL, encodedQuery, API_KEY);

        String response = sendRequest(url);

        try {
            return GSON.fromJson(response, SearchResult.class);
        } catch (JsonSyntaxException e) {
            Logger.logError("Failed to parse search response for: " + foodName, e);
            throw new ApiException("Failed to parse API response!", e);
        }
    }

    public FoodDetailsResponse getFoodReport(int fdcId) throws ApiException {
        if (fdcId <= 0) {
            throw new IllegalArgumentException("FDC ID must be positive!");
        }
        String url = String.format("%s/food/%d?api_key=%s", BASE_URL, fdcId, API_KEY);

        String response = sendRequest(url);

        try {
            return GSON.fromJson(response, FoodDetailsResponse.class);
        } catch (JsonSyntaxException e) {
            Logger.logError("Failed to parse food report for FDC ID: " + fdcId, e);
            throw new ApiException("Failed to parse API response!", e);
        }
    }

    private String sendRequest(String url) throws ApiException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HTTP_OK) {
                return response.body();
            } else if (statusCode == HTTP_BAD_REQUEST) {
                throw new ApiException("Bad request!");
            } else if (statusCode == HTTP_UNAUTHORIZED) {
                throw new ApiException("Unauthorized: Invalid API key");
            } else if (statusCode == HTTP_TOO_MANY_REQUESTS) {
                throw new ApiException("Rate limit exceeded. Please try again later.");
            } else if (statusCode == HTTP_NOT_FOUND) {
                throw new ApiException("Food not found");
            } else if (statusCode >= HTTP_SERVER_ERROR) {
                throw new ApiException("API server error");
            } else {
                throw new ApiException("API request failed with status: " + statusCode);
            }
        } catch (IOException e) {
            Logger.logError("Network error during API request", e);
            throw new ApiException("Network error:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.logError("API request interuppted", e);
            throw new ApiException("Request was interrupted.", e);
        }
    }
}
//FINITO TEST IT