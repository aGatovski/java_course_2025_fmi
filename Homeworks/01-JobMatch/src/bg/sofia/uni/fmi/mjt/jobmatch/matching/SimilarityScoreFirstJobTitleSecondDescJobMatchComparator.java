package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Comparator;

public class SimilarityScoreFirstJobTitleSecondDescJobMatchComparator implements Comparator<CandidateJobMatch> {
    @Override
    public int compare(CandidateJobMatch candidateJobMatch1, CandidateJobMatch candidateJobMatch2) {
        //2. If scores are equal, by job title in alphabetical order (case-sensitive)
        if (candidateJobMatch1.getSimilarityScore() == candidateJobMatch2.getSimilarityScore()) {
            String firstJobTitle = candidateJobMatch1.getJobPosting().getTitle();
            String secondJobTitle = candidateJobMatch2.getJobPosting().getTitle();
            return firstJobTitle.compareTo(secondJobTitle);
        }

        // Similarity score in descending order (higher similarity first)
        return Double.compare(candidateJobMatch2.getSimilarityScore(), candidateJobMatch1.getSimilarityScore());
    }
}
