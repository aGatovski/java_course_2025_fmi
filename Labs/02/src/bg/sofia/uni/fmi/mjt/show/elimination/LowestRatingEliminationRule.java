package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {
    public LowestRatingEliminationRule() {

    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        int lowestRating = Integer.MAX_VALUE;

        for (Ergenka ergenka : ergenkas) {
            int currentRating = ergenka.getRating();
            if (currentRating < lowestRating) {
                lowestRating = currentRating;
            }
        }

        int remainingCount = 0;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka.getRating() > lowestRating) {
                remainingCount++;
            }
        }

        Ergenka[] remainingErgenkas = new Ergenka[remainingCount];
        int index = 0;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka.getRating() > lowestRating) {
                remainingErgenkas[index++] = ergenka;
            }
        }

        return remainingErgenkas;
    }
}

