package bg.sofia.uni.fmi.mjt.news.client;

import bg.sofia.uni.fmi.mjt.news.exception.ApiKeyException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.model.Article;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.model.Source;
import bg.sofia.uni.fmi.mjt.news.query.Category;
import bg.sofia.uni.fmi.mjt.news.query.Country;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsApiImplementationTest {
    @Mock
    private NewsHttpClient mockHttpClient;

    @Mock
    private NewsSuccessResponse mockResponse;

    private NewsApiImplementation newsApi;
    private NewsQuery testQuery;

    @BeforeEach
    void setUp() {
        newsApi = new NewsApiImplementation(mockHttpClient);

        testQuery = new NewsQuery.NewsQueryBuilder()
            .setKeywords("test")
            .setCategory(Category.GENERAL)
            .setCountry(Country.US)
            .setPage(1)
            .build();
    }

    @Test
    void testConstructorValidKey() throws ApiKeyException {
        NewsApiImplementation api = new NewsApiImplementation("validKey");
        assertNotNull(api);
    }

    @Test
    void testConstructorThrowsOnNullApiKey() {
        assertThrows(ApiKeyException.class, () -> new NewsApiImplementation((String) null),
            "Should throw exception if API key is null");
    }

    @Test
    void testConstructorThrowsOnBlankApiKey() {
        assertThrows(ApiKeyException.class, () -> new NewsApiImplementation(""),
            "Should throw exception if API key is blank");
    }

    @Test
    void testSearchNewsReturnsFullResponseContent() throws NewsApiException {
        Source testSource = new Source("testID", "testName");

        Article testArticle = new Article(
            testSource,
            "testAuthor",
            "testTitle",
            "testDescription",
            "https://test.com",
            "https://test.com/image.jpg",
            "2026-01-18T12:00:00Z",
            "testContent"
        );

        NewsSuccessResponse expectedResponse = new NewsSuccessResponse(
            "ok",
            1,
            List.of(testArticle)
        );

        NewsQuery query = new NewsQuery.NewsQueryBuilder()
            .setKeywords("test")
            .build();

        when(mockHttpClient.sendRequest(query)).thenReturn(expectedResponse);

        NewsSuccessResponse actualResponse = newsApi.searchNews(query);

        verify(mockHttpClient).sendRequest(query);

        assertNotNull(actualResponse, "Response should not be null");
        assertEquals("ok", actualResponse.status());
        assertEquals(1, actualResponse.totalResults());
        assertEquals(1, actualResponse.articles().size());

        Article actualArticle = actualResponse.articles().getFirst();
        assertEquals("testTitle", actualArticle.title());
        assertEquals("testAuthor", actualArticle.author());
        assertEquals("testName", actualArticle.source().name());
    }

    @Test
    void testSearchNewsUsesCacheOnSameCall() throws NewsApiException {
        when(mockHttpClient.sendRequest(testQuery)).thenReturn(mockResponse);

        NewsSuccessResponse firstResult = newsApi.searchNews(testQuery);
        NewsSuccessResponse secondResult = newsApi.searchNews(testQuery);

        assertSame(firstResult, secondResult);
        verify(mockHttpClient, times(1)).sendRequest(testQuery);
    }

}
