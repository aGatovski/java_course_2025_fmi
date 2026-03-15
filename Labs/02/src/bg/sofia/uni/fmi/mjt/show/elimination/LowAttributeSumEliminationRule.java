package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule {
    private final int threshold;

    public LowAttributeSumEliminationRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        int remainingCount = 0;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka.getHumorLevel() + ergenka.getRomanceLevel() >= threshold) {
                remainingCount++;
            }
        }

        Ergenka[] remainingErgenkas = new Ergenka[remainingCount];
        int index = 0;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka.getHumorLevel() + ergenka.getRomanceLevel() >= threshold) {
                remainingErgenkas[index++] = ergenka;
            }
        }

        return remainingErgenkas;
    }
}
