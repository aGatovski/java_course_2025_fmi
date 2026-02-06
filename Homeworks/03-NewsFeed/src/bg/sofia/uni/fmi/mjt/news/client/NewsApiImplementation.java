package bg.sofia.uni.fmi.mjt.news.client;

import bg.sofia.uni.fmi.mjt.news.exception.ApiKeyException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import java.util.HashMap;
import java.util.Map;

public class NewsApiImplementation {
    private final NewsHttpClient httpClient;
    private final Map<NewsQuery, NewsSuccessResponse> cacheResponseMap;

    public NewsApiImplementation(String apiKey) throws ApiKeyException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiKeyException("Api key is required!");
        }

        this.httpClient = new NewsHttpClient(apiKey);
        this.cacheResponseMap = new HashMap<>();
    }

    public NewsSuccessResponse searchNews(NewsQuery query) throws NewsApiException {
        if (cacheResponseMap.containsKey(query)) {
            return cacheResponseMap.get(query);
        }

        NewsSuccessResponse response = httpClient.sendRequest(query);

        cacheResponseMap.put(query, response);

        return response;
    }
}
