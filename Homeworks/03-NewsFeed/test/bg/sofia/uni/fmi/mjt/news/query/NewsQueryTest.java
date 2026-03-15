package bg.sofia.uni.fmi.mjt.news.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NewsQueryTest {
    @Test
    void testBuilderWithNullKeywordsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NewsQuery.NewsQueryBuilder().setKeywords(null).build(),
            "Builder throws IllegalArgumentException when keywords are null!");
    }

    @Test
    void testBuilderWithBlankKeywordsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NewsQuery.NewsQueryBuilder().setKeywords("").build(),
            "Builder throws IllegalArgumentException when keywords are blank!");
    }

    @Test
    void testBuilderWithoutKeywordsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NewsQuery.NewsQueryBuilder().build(),
            "Builder throws IllegalArgumentException when called without keywords(mandatory)!");
    }

    @Test
    void testBuilderWithNonPositivePageThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new NewsQuery.NewsQueryBuilder().setKeywords("test").setPage(0).build(),
            "Builder throws IllegalArgumentException when page number is less than 1!");
        assertThrows(IllegalArgumentException.class,
            () -> new NewsQuery.NewsQueryBuilder().setKeywords("test").setPage(-1).build(),
            "Builder throws IllegalArgumentException when page number is less than 1!");
    }

    @Test
    void testBuilderWithNonPositivePageSizeThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new NewsQuery.NewsQueryBuilder().setKeywords("test").setPageSize(0).build(),
            "Builder throws IllegalArgumentException when page size is less than 1!");
        assertThrows(IllegalArgumentException.class,
            () -> new NewsQuery.NewsQueryBuilder().setKeywords("test").setPageSize(-1).build(),
            "Builder throws IllegalArgumentException when page size is less than 1!");
    }

    @Test
    void testBuilderWithPageSizeOverLimitThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new NewsQuery.NewsQueryBuilder().setKeywords("test").setPageSize(101).build(),
            "Builder throws IllegalArgumentException when page size is over 100!");
    }

    @Test
    void testNewsQueryEqual() {
        NewsQuery newsQuery1 =
            new NewsQuery.NewsQueryBuilder().setKeywords("bitcoin").setCategory(Category.BUSINESS).build();
        NewsQuery newsQuery2 =
            new NewsQuery.NewsQueryBuilder().setKeywords("bitcoin").setCategory(Category.BUSINESS).build();

        assertEquals(newsQuery1, newsQuery2);
        assertEquals(newsQuery1.hashCode(), newsQuery2.hashCode());
    }

    @Test
    void testNewsQueryNonEqual() {
        NewsQuery newsQuery1 =
            new NewsQuery.NewsQueryBuilder().setKeywords("bitcoin").setCategory(Category.BUSINESS).build();
        NewsQuery newsQuery2 =
            new NewsQuery.NewsQueryBuilder().setKeywords("ethereum").setCategory(Category.BUSINESS).build();

        assertNotEquals(newsQuery1, newsQuery2);
    }
}
