package bg.sofia.uni.fmi.mjt.news.model;

import java.util.List;

public record NewsSuccessResponse(String status, int totalResults, List<Article> articles) {
}
