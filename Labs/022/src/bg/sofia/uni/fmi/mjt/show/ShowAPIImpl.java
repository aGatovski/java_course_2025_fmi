package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class ShowAPIImpl implements ShowAPI {
    private Ergenka[] ergenkas;
    private final EliminationRule[] defaultEliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules) {
        this.ergenkas = ergenkas;

        if (defaultEliminationRules == null || defaultEliminationRules.length == 0) {
            EliminationRule defaultRule = new LowestRatingEliminationRule();
            this.defaultEliminationRules = new EliminationRule[] {defaultRule};
        } else {
            this.defaultEliminationRules = defaultEliminationRules;
        }
    }

    @Override
    public Ergenka[] getErgenkas() {
        return ergenkas;
    }

    @Override
    public void playRound(DateEvent dateEvent) {
        for (Ergenka ergenka : ergenkas) {
            organizeDate(ergenka, dateEvent);
        }

        eliminateErgenkas(defaultEliminationRules);
    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules) {
        if (eliminationRules == null || eliminationRules.length == 0) {
            for (EliminationRule eliminationRule : defaultEliminationRules) {
                this.ergenkas = eliminationRule.eliminateErgenkas(ergenkas);
            }
        } else {
            for (EliminationRule eliminationRule : eliminationRules) {
                this.ergenkas = eliminationRule.eliminateErgenkas(ergenkas);
            }
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {
        if (ergenka != null) {
            ergenka.reactToDate(dateEvent);
        }
    }
}
