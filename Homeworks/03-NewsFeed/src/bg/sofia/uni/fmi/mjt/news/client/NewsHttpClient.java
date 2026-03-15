package bg.sofia.uni.fmi.mjt.news.client;

import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;

public interface NewsHttpClient {
    NewsSuccessResponse sendRequest(NewsQuery query) throws NewsApiException;
}
