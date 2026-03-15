package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;

public class CandidateSimilarityMatch {
    private static final int MIN_SIMILARITY_SCORE = 0;
    private static final int MAX_SIMILARITY_SCORE = 1;

    private Candidate targetCandidate;
    private Candidate similarCandidate;
    private double similarityScore;

    public CandidateSimilarityMatch(Candidate targetCandidate, Candidate similarCandidate, double similarityScore) {
        setTargetCandidate(targetCandidate);
        setSimilarCandidate(similarCandidate);
        setSimilarityScore(similarityScore);
    }

    public Candidate getTargetCandidate() {
        return targetCandidate;
    }

    public Candidate getSimilarCandidate() {
        return similarCandidate;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    private void setTargetCandidate(Candidate targetCandidate) {
        if (targetCandidate == null) {
            throw new IllegalArgumentException("TargetCandidateJobMatch candidate cannot be null");
        }

        this.targetCandidate = targetCandidate;
    }

    private void setSimilarCandidate(Candidate similarCandidate) {
        if (similarCandidate == null) {
            throw new IllegalArgumentException("Similar candidate cannot be null");
        }

        this.similarCandidate = similarCandidate;
    }

    private void setSimilarityScore(double similarityScore) {
        if (similarityScore < MIN_SIMILARITY_SCORE || similarityScore > MAX_SIMILARITY_SCORE) {
            throw new IllegalArgumentException("Similarity score must be between 0 and 1");
        }

        this.similarityScore = similarityScore;
    }
}
