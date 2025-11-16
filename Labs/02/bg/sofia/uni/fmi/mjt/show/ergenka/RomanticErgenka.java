package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class RomanticErgenka implements Ergenka {
    private final String name;
    private final short age;
    private final int romanceLevel;
    private final int humorLevel;
    private int rating;
    private final String favoriteDateLocation;

    public RomanticErgenka(String name, short age, int romanceLevel, int humorLevel, int rating, String favoriteDateLocation) {
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
        int comparison = favoriteDateLocation.compareToIgnoreCase(dateEvent.getLocation());
        int dateDuration = dateEvent.getDuration();
        int dateTension = dateEvent.getTensionLevel();

        if (comparison == 0) {
            bonuses += 5;
        }

        if (dateDuration < 30) {
            bonuses -= 3;
        } else if (dateDuration > 90) {
            bonuses -= 2;
        }

        rating = (romanceLevel * 7) / dateTension + (int) Math.floor((double) humorLevel / 3) + bonuses;
    }
}
