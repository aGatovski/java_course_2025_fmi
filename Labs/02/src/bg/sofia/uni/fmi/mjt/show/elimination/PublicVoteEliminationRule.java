package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class PublicVoteEliminationRule implements EliminationRule {
    private final String[] votes;

    public PublicVoteEliminationRule(String[] votes) {
        this.votes = votes;
    }

    private int getMajorityVoteCount(String[] votes, String candidate) {
        int counter = 0;

        for (String vote : votes) {
            if (vote.equals(candidate)) {
                counter++;
            }
        }

        return counter;
    }

    //Boyer Moore Algorithm
    private String findMajorityVote(String[] votes) {
        int counter = 1;
        String candidate = votes[0];

        for (int i = 1; i < votes.length; i++) {
            if (counter == 0) {
                candidate = votes[i];
                counter = 1;
            } else {
                if (!votes[i].equals(candidate)) {
                    counter--;
                } else {
                    counter++;
                }
            }
        }

        return candidate;
    }

    private int getRemainingErgenkasCount(Ergenka[] ergenkas, String eliminatedErgenka) {
        int remainingCount = 0;
        boolean eliminatedOnce = false;

        for (Ergenka ergenka : ergenkas) {
            if (!ergenka.getName().equals(eliminatedErgenka)) {
                remainingCount++;
            } else if (!eliminatedOnce) {
                eliminatedOnce = true;
            } else {
                remainingCount++;
            }
        }

        return  remainingCount;
    }

    private Ergenka[] getRemainingErgenkas(Ergenka[] ergenkas, String eliminatedErgenka) {
        int index = 0;
        boolean eliminatedOnce = false;
        Ergenka[] remainingErgenkas = new Ergenka[getRemainingErgenkasCount(ergenkas, eliminatedErgenka)];

        for (Ergenka ergenka : ergenkas) {
            if (!ergenka.getName().equals(eliminatedErgenka)) {
                remainingErgenkas[index++] = ergenka;
            } else if (!eliminatedOnce) {
                eliminatedOnce = true;
            } else {
                remainingErgenkas[index++] = ergenka;
            }
        }

        return remainingErgenkas;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        if (votes == null || votes.length == 0) {
            return ergenkas;
        }

        int votesNumber = votes.length;
        String candidate = findMajorityVote(votes);
        int majorityVoteCount = getMajorityVoteCount(votes, candidate);

        if (majorityVoteCount >= (votesNumber / 2) + 1) {

            return getRemainingErgenkas(ergenkas, candidate);
        } else {
            return ergenkas;
        }
    }
}
