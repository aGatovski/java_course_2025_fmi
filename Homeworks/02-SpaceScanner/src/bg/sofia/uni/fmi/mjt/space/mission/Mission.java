package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    // Matches a comma only if it is followed by an even number of quotes
    private static final String MISSIONS_SPLIT_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String DETAILS_DELIMITER = "\\|";
    private static final int ID_COLUMN = 0;
    private static final int COMPANY_COLUMN = 1;
    private static final int LOCATION_COLUMN = 2;
    private static final int DATE_COLUMN = 3;
    private static final int DETAIL_COLUMN = 4;
    private static final int ROCKET_STATUS_COLUMN = 5;
    private static final int COST_COLUMN = 6;
    private static final int MISSION_STATUS_COLUMN = 7;
    private static final int EXPECTED_COLLUM_COUNT = 8;

    public Mission {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank!");
        }

        if (company == null || company.isBlank()) {
            throw new IllegalArgumentException("Company cannot be null or blank!");
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or blank!");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (detail == null) {
            throw new IllegalArgumentException("Detail cannot be null");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null");
        }

        if (cost == null) {
            throw new IllegalArgumentException("Cost container cannot be null");
        }

        if (cost.isPresent() && cost.get() < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }

        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }
    }

    public static Mission of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("CSV line cannot be null or blank!");
        }

        String[] tokens = line.split(MISSIONS_SPLIT_REGEX);

        if (tokens.length != EXPECTED_COLLUM_COUNT) {
            throw new IllegalArgumentException(
                String.format("Invalid CSV line. Expected 8 fields but found %d", tokens.length));
        }

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replace("\"", "").trim();
        }

        String[] tokensDetail = tokens[DETAIL_COLUMN].split(DETAILS_DELIMITER);
        LocalDate missionDate = LocalDate.parse(tokens[DATE_COLUMN], DateTimeFormatter.ofPattern("E MMM dd, yyyy"));
        Optional<Double> missionCost = tokens[COST_COLUMN].isBlank()
            ? Optional.empty()
            : Optional.of(Double.parseDouble(tokens[COST_COLUMN].replace(",", "")));

        return new Mission(tokens[ID_COLUMN], tokens[COMPANY_COLUMN], tokens[LOCATION_COLUMN],
            LocalDate.parse(tokens[DATE_COLUMN], DateTimeFormatter.ofPattern("E MMM dd, yyyy")),
            new Detail(tokensDetail[0].trim(), tokensDetail[1].trim()),
            RocketStatus.valueOf(tokens[ROCKET_STATUS_COLUMN]), missionCost,
            MissionStatus.valueOf(tokens[MISSION_STATUS_COLUMN]));
    }
}
