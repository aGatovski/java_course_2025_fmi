package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class PublicVoteEliminationRule implements EliminationRule {
    private final String[] votes;
    public PublicVoteEliminationRule(String[] votes){
        this.votes = new String[votes.length];
       System.arraycopy(votes,0,this.votes,0,votes.length);
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas){
        Ergenka[] afterEliminationErgenkas = new Ergenka[ergenkas.length];
        int votesNumber = votes.length;
        int counter = 0;
        String candidate = "";

        for (String vote : votes){
            if(counter == 0){
                candidate = vote;
            } else {
                if(vote != candidate){
                    counter--;
                }else {
                    counter++;
                }
            }
        }

        counter = 0;
        for (String vote: votes) {
            if (vote == candidate) {
                counter++;
            }
        }

        int index = 0;
        if(counter > (votesNumber/2) + 1){
            for (Ergenka ergenka : ergenkas){
                if(ergenka.getName() != candidate){
                    afterEliminationErgenkas[index++] = ergenka;
                }
            }
        } else {
            return  ergenkas;
        }
        return afterEliminationErgenkas;

    }

}
