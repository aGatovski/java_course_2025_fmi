package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final String ROCKET_SPLIT_REGEX = ",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

    private static final int ROCKET_ID_COLUMN = 0;
    private static final int ROCKET_NAME_COLUMN = 1;
    private static final int ROCKET_WIKI_COLUMN = 2;
    private static final int ROCKET_HEIGHT_COLUMN = 3;
    private static final int EXPECTED_COLLUM_COUNT = 4;
    private static final int LIMIT = -1;
    private static final String ROCKET_DELIMITER = ",";

    public Rocket {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank!");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank!");
        }

        if (wiki.isPresent() && wiki.get().isBlank()) {
            throw new IllegalArgumentException("Wiki cannot be blank!");
        }

        if (height.isPresent() && height.get() < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
    }

    public static Rocket of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("CSV line cannot be null or blank!");
        }

        String[] tokens = line.split(ROCKET_SPLIT_REGEX, LIMIT);

        if (tokens.length != EXPECTED_COLLUM_COUNT) {
            throw new IllegalArgumentException(
                String.format("Invalid CSV line. Expected 4 fields but found %d", tokens.length));
        }

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }

        Optional<String> rocketWiki = Optional.empty();
        if (!tokens[ROCKET_WIKI_COLUMN].isBlank()) {
            rocketWiki = Optional.of(tokens[ROCKET_WIKI_COLUMN]);
        }

        Optional<Double> rocketHeight = Optional.empty();
        if (!tokens[ROCKET_HEIGHT_COLUMN].isBlank()) {
            rocketHeight =
                Optional.of(Double.parseDouble(tokens[ROCKET_HEIGHT_COLUMN].replace(" m", "")));
        }

        return new Rocket(tokens[ROCKET_ID_COLUMN], tokens[ROCKET_NAME_COLUMN], rocketWiki, rocketHeight);
    }
}
