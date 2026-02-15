package bg.sofia.uni.fmi.mjt.foodanalyzer.server.api.dto;

import java.util.List;

public record SearchResult(int totalHits, int currentPage, int totalPages, List<SearchResultFood> foods) {
}
