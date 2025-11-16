package bg.sofia.uni.fmi.mjt.show;


import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class ShowAPIImpl implements ShowAPI {
    private Ergenka[] ergenkas;
    private final EliminationRule[] defaultEliminationRules;
    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules){
        this.ergenkas = new Ergenka[ergenkas.length];
        System.arraycopy(ergenkas,0,this.ergenkas,0,ergenkas.length);
        this.defaultEliminationRules = new EliminationRule[defaultEliminationRules.length];
        System.arraycopy(defaultEliminationRules,0,this.defaultEliminationRules,0,defaultEliminationRules.length);
    }

    /**
     * Returns the current ergenkas participating in the show.
     *
     * @return an array of ergenkas, never {@code null}; may be empty

     */
    @Override
    public Ergenka[] getErgenkas(){
        return ergenkas;
    }

    /**
     * Plays a full round using the provided {@link DateEvent}.
     *
     * @param dateEvent the event to play during the round, never {@code null}
     */
    @Override
    public void playRound(DateEvent dateEvent){
        for(Ergenka ergenka : ergenkas){
            organizeDate(ergenka,dateEvent);
        }
        eliminateErgenkas(defaultEliminationRules);
    }

    /**
     * Applies a sequence of elimination rules to the current ergenkas.
     *
     * @param eliminationRules the rules to apply
     */
    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules){
        if(eliminationRules.length == 0 ){
            for(EliminationRule eliminationRule : defaultEliminationRules){
                ergenkas = eliminationRule.eliminateErgenkas(ergenkas); // update ergenkas  = !
            }
        }else {
            for(EliminationRule eliminationRule : eliminationRules){
                ergenkas = eliminationRule.eliminateErgenkas(ergenkas); // update ergenkas  = !
            }
        }
    }

    /**
     * Performs a single date with the ergenkas {@link DateEvent}.
     *
     * @param ergenka the ergenka participating in the date, never {@code null}
     * @param dateEvent the date event to organize, never {@code null}
     */
    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent){
        ergenka.reactToDate(dateEvent);
    }
}
