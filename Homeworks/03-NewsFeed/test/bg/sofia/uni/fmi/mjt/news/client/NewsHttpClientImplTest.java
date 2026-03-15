package bg.sofia.uni.fmi.mjt.news.client;

import bg.sofia.uni.fmi.mjt.news.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.news.exception.TooManyRequestException;
import bg.sofia.uni.fmi.mjt.news.exception.UnauthorizedException;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.query.Category;
import bg.sofia.uni.fmi.mjt.news.query.Country;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearAllCaches;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsHttpClientImplTest {

    private static final String RESPONSE_JSON = """
        {
            "status": "ok",
            "totalResults": 1,
            "articles": []
        }
        """;

    private static final String ERROR_JSON = """
        {
            "status": "error",
            "code": "parametersMissing",
            "message": "Detailed error message from API"
        }
        """;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private NewsHttpClientImpl client;
    private NewsQuery testQuery;

    @BeforeEach
    void setUp() {
        client = new NewsHttpClientImpl("test-api-key", mockHttpClient);

        testQuery = new NewsQuery.NewsQueryBuilder()
            .setKeywords("test")
            .setCategory(Category.GENERAL)
            .setCountry(Country.US)
            .build();
    }

    @Test
    void testSendRequestSuccessful() throws IOException, InterruptedException, NewsApiException {
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(RESPONSE_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(
            mockHttpResponse);

        NewsSuccessResponse response = client.sendRequest(testQuery);

        assertNotNull(response);
        assertEquals("ok", response.status());
        assertEquals(1, response.totalResults());
    }

    @Test
    void testSendRequestHandlesIOException() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("I/O error occurred"));

        NewsApiException exception = assertThrows(NewsApiException.class, () -> {
            client.sendRequest(testQuery);
        });
        assertEquals("I/O error occurred when sending query or the client has shut down!", exception.getMessage());
    }


    @Test
    void testSendRequestThrowsBadRequestExceptionWhenResponseCode400() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(400);
        when(mockHttpResponse.body()).thenReturn(ERROR_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        NewsQuery query = new NewsQuery.NewsQueryBuilder().setKeywords("test").build();
        assertThrows(BadRequestException.class, () -> client.sendRequest(query), "Response returned status code 400");
    }

    @Test
    void testSendRequestThrowsUnauthorizedExceptionWhenResponseCode401() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(401);
        when(mockHttpResponse.body()).thenReturn(ERROR_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        NewsQuery query = new NewsQuery.NewsQueryBuilder().setKeywords("test").build();
        assertThrows(UnauthorizedException.class, () -> client.sendRequest(query), "Response returned status code 401");
    }

    @Test
    void testSendRequestThrowsTooManyRequestExceptionWhenResponseCode401() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(429);
        when(mockHttpResponse.body()).thenReturn(ERROR_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        NewsQuery query = new NewsQuery.NewsQueryBuilder().setKeywords("test").build();
        assertThrows(TooManyRequestException.class, () -> client.sendRequest(query), "Response returned status code 429");
    }

    @Test
    void testSendRequestThrowsServerErrorExceptionWhenResponseCode401() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn(ERROR_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        NewsQuery query = new NewsQuery.NewsQueryBuilder().setKeywords("test").build();
        assertThrows(ServerErrorException.class, () -> client.sendRequest(query), "Response returned status code 500");
    }

    @Test
    void testSendRequestThrowsNewsApiExceptionWhenResponseCodeUnknown() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(0);
        when(mockHttpResponse.body()).thenReturn(ERROR_JSON);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockHttpResponse);

        NewsQuery query = new NewsQuery.NewsQueryBuilder().setKeywords("test").build();
        assertThrows(NewsApiException.class, () -> client.sendRequest(query), "Response returned unknown status code");
    }
}