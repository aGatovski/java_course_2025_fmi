package bg.sofia.uni.fmi.mjt.news.query;

import java.util.Objects;

public class NewsQuery {
    //mendatory
    private final String keywords;
    private final Category category;
    private final Country country;
    private final int page;
    private final int pageSize;

    private NewsQuery(NewsQueryBuilder newsQueryBuilder) {
        this.keywords = newsQueryBuilder.keywords;
        this.category = newsQueryBuilder.category;
        this.country = newsQueryBuilder.country;
        this.page = newsQueryBuilder.page;
        this.pageSize = newsQueryBuilder.pageSize;
    }

    public String getKeywords() {
        return keywords;
    }

    public Category getCategory() {
        return category;
    }

    public Country getCountry() {
        return country;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        NewsQuery newsQuery = (NewsQuery) obj;

        return keywords.equals(newsQuery.keywords) && category == newsQuery.category && country == newsQuery.country &&
            page == newsQuery.page && pageSize == newsQuery.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywords, category, country, page, pageSize);
    }

    public static class NewsQueryBuilder {
        private static final int MIN_NUMBER_PAGES = 1;
        private static final int MAX_NUMBER_PAGES = 100;
        private static final int DEFAULT_NUMBER_PAGES = 20;

        private String keywords;
        private Category category;
        private Country country;
        private int page = MIN_NUMBER_PAGES;
        private int pageSize = DEFAULT_NUMBER_PAGES;

        public NewsQueryBuilder setKeywords(String keywords) {
            if (keywords == null || keywords.isBlank()) {
                throw new IllegalArgumentException("Keywords cannot be null or blank");
            }

            this.keywords = keywords;
            return this;
        }

        public NewsQueryBuilder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public NewsQueryBuilder setCountry(Country country) {
            this.country = country;
            return this;
        }

        public NewsQueryBuilder setPage(int page) {
            if (page < MIN_NUMBER_PAGES) {
                throw new IllegalArgumentException("Page must be >= 1!");
            }

            this.page = page;
            return this;
        }

        public NewsQueryBuilder setPageSize(int pageSize) {
            if (pageSize < MIN_NUMBER_PAGES || pageSize > MAX_NUMBER_PAGES) {
                throw new IllegalArgumentException("Page size must be between 1 and 100!");
            }

            this.pageSize = pageSize;
            return this;
        }

        public NewsQuery build() {
            if (keywords == null || keywords.isBlank()) {
                throw new IllegalArgumentException("Keywords must be set! Keywords are mandatory!");
            }

            return new NewsQuery(this);
        }
    }
}
