package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api;

import bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions.ApiException;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodDataApiClientTest {
    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private FoodDataApiClient apiClient;

    @BeforeEach
    void setUp() throws Exception {
        apiClient = new FoodDataApiClient();
    }

    @Test
    void testGetFoodWithValidFoodName() throws IOException, InterruptedException, ApiException {
        String foodName = "raffaello treat";
        String jsonResponse = "{" +
            "\"totalHits\":2," +
            "\"foods\":[" +
            "{\"fdcId\":123,\"description\":\"Red Apple\"}," +
            "{\"fdcId\":456,\"description\":\"Green Apple\"}" +
            "]" +
            "}";

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        SearchResult result = apiClient.getFood(foodName);

        assertNotNull(result, "Result should not be null on successful API call");
    }
}