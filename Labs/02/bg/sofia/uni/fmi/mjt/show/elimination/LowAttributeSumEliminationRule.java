package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements  EliminationRule{
    private final int threshold;
    public LowAttributeSumEliminationRule(int threshold){
        this.threshold = threshold;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        Ergenka[] afterEliminationErgenkas = new Ergenka[ergenkas.length]; // the same size since no one might be eliminated
        int counter = 0;

        for (Ergenka erg : ergenkas){
            int humourLevel = erg.getHumorLevel();
            int romanceLevel = erg.getRomanceLevel();
            if(humourLevel + romanceLevel >= threshold){
                afterEliminationErgenkas[counter++] = erg;
            }
        }
        return afterEliminationErgenkas;
    }
}
