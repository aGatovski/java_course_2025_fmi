package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.FoodDetailsResponse;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.SearchResult;
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

    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;

    public FoodDataApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public SearchResult getFood(String foodName) throws ApiException {
        String encodedQuery = URLEncoder.encode(foodName, StandardCharsets.UTF_8);

        String url =
            String.format("%s/foods/search?query=%s&requireAllWords=true&api_key=%s", BASE_URL, encodedQuery, API_KEY);

        String response = sendRequest(url);

        try {
            return GSON.fromJson(response, SearchResult.class);
        } catch (JsonSyntaxException e) {
            throw new ApiException("Failed to parse API response!", e);
        }
    }

    public FoodDetailsResponse getFoodReport(int fdcId) throws ApiException {
        String url = String.format("%s/food/%d?api_key=%s", BASE_URL, fdcId, API_KEY);

        String response = sendRequest(url);

        try {
            return GSON.fromJson(response, FoodDetailsResponse.class);
        } catch (JsonSyntaxException e) {
            throw new ApiException("Failed to parse API response!", e);
        }
    }

    private String sendRequest(String url) throws ApiException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            throw new ApiException("I/O error occurred when sending query or the client has shut down!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            //throw new IOException("Request interrupted", e);
            throw new ApiException("Query operation was interrupted!");
        }
    }
}
