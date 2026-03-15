package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class RomanticErgenka implements Ergenka {
    private static final int FAVORITE_LOCATION_BONUS = 5;
    private static final int SHORT_DATE_TIME_PENALTY = -3;
    private static final int LONG_DATE_TIME_PENALTY = -2;
    private static final int MIN_DATE_TIME = 30;
    private static final int MAX_DATE_TIME = 120;
    private static final int ROMANCE_MULTIPLIER = 7;
    private static final int HUMOUR_DELIMITER = 3;

    private final String name;
    private final short age;
    private final int romanceLevel;
    private final int humorLevel;
    private int rating;
    private final String favoriteDateLocation;

    public RomanticErgenka(String name, short age, int romanceLevel, int humorLevel, int rating,
                           String favoriteDateLocation) {
        this.name = name;
        this.age = age;
        this.romanceLevel = romanceLevel;
        this.humorLevel = humorLevel;
        this.rating = rating;
        this.favoriteDateLocation = favoriteDateLocation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getAge() {
        return age;
    }

    @Override
    public int getRomanceLevel() {
        return romanceLevel;
    }

    @Override
    public int getHumorLevel() {
        return humorLevel;
    }

    @Override
    public int getRating() {
        return rating;
    }

    @Override
    public void reactToDate(DateEvent dateEvent) {
        int bonuses = 0;
        //int comparison = favoriteDateLocation.compareToIgnoreCase(dateEvent.getLocation());
        int dateDuration = dateEvent.getDuration();
        int dateTension = dateEvent.getTensionLevel();

        if (favoriteDateLocation != null && favoriteDateLocation.compareToIgnoreCase(dateEvent.getLocation()) == 0) {
            bonuses += FAVORITE_LOCATION_BONUS;
        }

        if (dateDuration < MIN_DATE_TIME) {
            bonuses += SHORT_DATE_TIME_PENALTY;
        } else if (dateDuration > MAX_DATE_TIME) {
            bonuses += LONG_DATE_TIME_PENALTY;
        }

        if (dateTension == 0) {
            dateTension = 1;
        }

        this.rating += (romanceLevel * ROMANCE_MULTIPLIER) / dateTension +
            Math.floorDiv(humorLevel, HUMOUR_DELIMITER) + bonuses;
    }
}
