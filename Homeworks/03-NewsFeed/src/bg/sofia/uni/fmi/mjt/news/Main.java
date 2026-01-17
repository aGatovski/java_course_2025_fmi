package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.client.NewsApiClient;
import bg.sofia.uni.fmi.mjt.news.exception.ApiKeyException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.news.model.Article;
import bg.sofia.uni.fmi.mjt.news.model.NewsSuccessResponse;
import bg.sofia.uni.fmi.mjt.news.query.Category;
import bg.sofia.uni.fmi.mjt.news.query.Country;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;

public class Main {

    public static void main(String[] args) {
        try {
            // 1. Get API Key (Set this in IntelliJ Run Configuration -> Environment Variables)
            String apiKey = "0c0b5e04b47c43ad8d6b0e232f91eafc";

            // If testing without Env Var, uncomment below:
            // apiKey = "YOUR_TEST_KEY_HERE";

            // 2. Initialize Client (Constructor, not Builder)
            NewsApiClient client = new NewsApiClient(apiKey);

            // === Example 1: Search by keywords ===
            System.out.println("=== Example 1: Search by keywords ===");

            NewsQuery query1 = new NewsQuery.NewsQueryBuilder() // Use the specific inner class name
                .setKeywords("bitcoin") // Use setKeywords, not keywords
                .build();

            NewsSuccessResponse response1 = client.searchNews(query1);
            printResults(response1);

            // === Example 2: Search with category and country ===
            System.out.println("\n=== Example 2: Search with category and country ===");

            NewsQuery query2 = new NewsQuery.NewsQueryBuilder()
                .setKeywords("technology")
                .setCategory(Category.TECHNOLOGY)
                .setCountry(Country.US)
                .setPageSize(5)
                .build();

            NewsSuccessResponse response2 = client.searchNews(query2);
            printResults(response2);

            // === Example 3: Pagination ===
            System.out.println("\n=== Example 3: Pagination (page 2) ===");

            NewsQuery query3 = new NewsQuery.NewsQueryBuilder()
                .setKeywords("sports")
                .setCategory(Category.SPORTS)
                .setPage(2)
                .setPageSize(5)
                .build();

            NewsSuccessResponse response3 = client.searchNews(query3);
            printResults(response3);

        } catch (ApiKeyException e) {
            System.err.println("API Key Error: " + e.getMessage());
            System.err.println("Please set the NEWS_API_KEY environment variable.");
        } catch (NewsApiException e) {
            System.err.println("API Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printResults(NewsSuccessResponse response) {
        // Records use accessor methods without 'get' prefix
        System.out.println("Status: " + response.status());
        System.out.println("Total Results: " + response.totalResults());
        System.out.println("Articles returned: " + response.articles().size());
        System.out.println();

        int count = 1;
        for (Article article : response.articles()) {
            System.out.println(count++ + ". " + article.title());
            // Check for null source before accessing name
            String sourceName = (article.source() != null) ? article.source().name() : "Unknown";
            System.out.println("   Source: " + sourceName);
            System.out.println("   URL: " + article.url());
            System.out.println();
        }
    }
}