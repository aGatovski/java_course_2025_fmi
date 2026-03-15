package bg.sofia.uni.fmi.mjt.news.client;

import bg.sofia.uni.fmi.mjt.news.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.news.exception.TooManyRequestException;
import bg.sofia.uni.fmi.mjt.news.exception.UnauthorizedException;
import bg.sofia.uni.fmi.mjt.news.model.NewsErrorResponse;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.model.ResponseCode;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class NewsHttpClientImpl implements NewsHttpClient {
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines?";
    private static final String API_KEY_HEADER = "X-Api-Key";

    private final String apiKey;
    private static final Gson GSON = new Gson();
    private final HttpClient httpClient;

    public NewsHttpClientImpl(String apiKey) {
        this.apiKey = apiKey;
        httpClient = HttpClient.newHttpClient();
    }

    NewsHttpClientImpl(String apiKey, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    @Override
    public NewsSuccessResponse sendRequest(NewsQuery query) throws NewsApiException {
        String url = buildURL(query);

        HttpRequest request =
            HttpRequest.newBuilder().uri(URI.create(url)).header(API_KEY_HEADER, apiKey).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        } catch (IOException e) {
            throw new NewsApiException("I/O error occurred when sending query or the client has shut down!");
        } catch (InterruptedException e) {
            throw new NewsApiException("Query operation was interrupted!");
        }
    }

    private String buildURL(NewsQuery query) {
        StringBuilder queryString = new StringBuilder(BASE_URL);

        String encodedKeywords = URLEncoder.encode(query.getKeywords(), StandardCharsets.UTF_8);
        queryString.append("q=").append(encodedKeywords);

        if (query.getCountry() != null) {
            queryString.append("&country=").append(query.getCountry());
        }

        if (query.getCategory() != null) {
            queryString.append("&category=").append(query.getCategory());
        }

        queryString.append("&pageSize=").append(query.getPageSize());
        queryString.append("&page=").append(query.getPage());

        return queryString.toString();
    }

    private NewsSuccessResponse handleResponse(HttpResponse<String> response) throws NewsApiException {
        int statusCode = response.statusCode();
        String body = response.body();

        if (statusCode == ResponseCode.OK.getCode()) {
            return GSON.fromJson(body, NewsSuccessResponse.class);
        } else {
            handleErrorResponse(statusCode, body);
            throw new NewsApiException("Unexpected error!");
        }
    }

    private void handleErrorResponse(int statusCode, String body) throws NewsApiException {
        NewsErrorResponse errorResponse = GSON.fromJson(body, NewsErrorResponse.class);

        ResponseCode responseCode = ResponseCode.fromInt(statusCode);

        switch (responseCode) {
            case BAD_REQUEST:
                throw new BadRequestException("Bad request: " + errorResponse.message());
            case UNAUTHORIZED:
                throw new UnauthorizedException("Unauthorized: " + errorResponse.message());
            case TOO_MANY_REQUESTS:
                throw new TooManyRequestException("Too many requests: " + errorResponse.message());
            case SERVER_ERROR:
                throw new ServerErrorException("Server error: " + errorResponse.message());
            default:
                throw new NewsApiException("Unexpected error: " + errorResponse.message());
        }
    }
}
