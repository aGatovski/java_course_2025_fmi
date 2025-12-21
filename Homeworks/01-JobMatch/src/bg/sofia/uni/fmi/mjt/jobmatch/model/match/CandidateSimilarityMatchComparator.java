package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;

import java.util.Comparator;

public class CandidateSimilarityMatchComparator implements Comparator<CandidateSimilarityMatch> {
    @Override
    public int compare(CandidateSimilarityMatch candidateSimilarityMatch1,
                       CandidateSimilarityMatch candidateSimilarityMatch2) {


        //thenComparing()


        //2. If scores are equal, by job title in alphabetical order (case-sensitive)
        if (candidateSimilarityMatch1.getSimilarityScore() == candidateSimilarityMatch2.getSimilarityScore()) {
            String firstSimilarCandidateMatchName = candidateSimilarityMatch1.getSimilarCandidate().getName();
            String secondSimilarCandidateMatchName = candidateSimilarityMatch2.getSimilarCandidate().getName();
            return firstSimilarCandidateMatchName.compareTo(secondSimilarCandidateMatchName);
        }

        // Similarity score in descending order (higher similarity first)
        return Double.compare(candidateSimilarityMatch2.getSimilarityScore(),
            candidateSimilarityMatch1.getSimilarityScore());
    }
}



public class CandidateNameAlp  implements  Comparator<CandidateSimilarityMatch> {
    @Override
    public int compare(CandidateSimilarityMatch c1, CandidateSimilarityMatch c2) {
        return c1.getSimilarCandidate().getName().compareTo(c2.getSimilarCandidate().getName());
    }
}