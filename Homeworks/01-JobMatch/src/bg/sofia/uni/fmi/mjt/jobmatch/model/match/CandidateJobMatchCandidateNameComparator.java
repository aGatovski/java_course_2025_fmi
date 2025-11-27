package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import java.util.Comparator;

public class CandidateJobMatchCandidateNameComparator implements Comparator<CandidateJobMatch> {
    @Override
    public int compare(CandidateJobMatch candidateJobMatch1, CandidateJobMatch candidateJobMatch2) {
        //If scores are equal, by candidate name in alphabetical order (case-sensitive)
        if (candidateJobMatch1.getSimilarityScore() == candidateJobMatch2.getSimilarityScore()) {
            String firstCandidateName = candidateJobMatch1.getCandidate().getName();
            String secondCandidateName = candidateJobMatch2.getCandidate().getName();
            return firstCandidateName.compareTo(secondCandidateName);
        }

        // Similarity score in descending order (higher similarity first)
        return Double.compare(candidateJobMatch2.getSimilarityScore(), candidateJobMatch1.getSimilarityScore());
    }
}
