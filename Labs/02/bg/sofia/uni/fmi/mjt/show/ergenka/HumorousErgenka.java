package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka implements Ergenka {
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
        if (dateDuration >= 30 && dateDuration <= 90) {
            bonuses = 4;
        } else if (dateDuration < 30) {
            bonuses = -2;
        } else {
            bonuses = -3;
        }

        rating = (humorLevel * 5) / dateTensionLevel + (int) Math.floor((double) romanceLevel / 3) + bonuses; // Math.floor връща double?
    }
}
