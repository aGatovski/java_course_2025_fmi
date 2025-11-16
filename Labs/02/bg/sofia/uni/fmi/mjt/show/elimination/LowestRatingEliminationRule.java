package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {
    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        Ergenka[] afterEliminationErgenkas = new Ergenka[ergenkas.length]; // the same size since no one might be eliminated
        int lowestRating = Integer.MAX_VALUE;
        int[] ratings = new int[ergenkas.length];

        for (int i = 0; i < ratings.length ; i++ ){
            ratings[i] = ergenkas[i].getRating();
        }

        for (int rat : ratings){
            if(rat < lowestRating){
                lowestRating = rat;
            }
        }

        int counter = 0;

        for (Ergenka erg : ergenkas){
            if(erg.getRating() > lowestRating){
                afterEliminationErgenkas[counter++] = erg;
            }
        }

        return afterEliminationErgenkas;
    }
}
