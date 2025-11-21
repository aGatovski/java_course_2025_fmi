package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka implements Ergenka {
    private static final int PERFECT_DATE_TIME = 4;
    private static final int SHORT_DATE_TIME_PENALTY = -2;
    private static final int LONG_DATE_TIME_PENALTY = -3;
    private static final int MIN_DATE_TIME = 30;
    private static final int MAX_DATE_TIME = 90;
    private static final int HUMOUR_MULTIPLIER = 5;
    private static final int ROMANCE_DELIMITER = 3;


    private final String name;
    private final short age;
    private final int romanceLevel;
    private final int humorLevel;
    private int rating;

    public HumorousErgenka(String name, short age, int romanceLevel, int humorLevel, int rating) {
        this.name = name;
        this.age = age;
        this.romanceLevel = romanceLevel;
        this.humorLevel = humorLevel;
        this.rating = rating;
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
        int dateDuration = dateEvent.getDuration();
        int dateTensionLevel = dateEvent.getTensionLevel();

        if (dateDuration >= MIN_DATE_TIME && dateDuration <= MAX_DATE_TIME) {
            bonuses += PERFECT_DATE_TIME;
        } else if (dateDuration < MIN_DATE_TIME) {
            bonuses += SHORT_DATE_TIME_PENALTY;
        } else {
            bonuses += LONG_DATE_TIME_PENALTY;
        }

        if (dateTensionLevel == 0) {
            dateTensionLevel = 1;
        }

        this.rating += (humorLevel * HUMOUR_MULTIPLIER) / dateTensionLevel +
            Math.floorDiv(romanceLevel, ROMANCE_DELIMITER) + bonuses;
    }

}
