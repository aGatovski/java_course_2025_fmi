package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;

public class CandidateJobMatch {
    private static final int MIN_SIMILARITY_SCORE = 0;
    private static final int MAX_SIMILARITY_SCORE = 1;

    private Candidate candidate;
    private JobPosting jobPosting;
    private double similarityScore;

    public CandidateJobMatch(Candidate candidate, JobPosting jobPosting, double similarityScore) {
        setCandidate(candidate);
        setJobPosting(jobPosting);
        setSimilarityScore(similarityScore);
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    private void setCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null!");
        }

        this.candidate = candidate;
    }

    private void setJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting cannot be null!");
        }

        this.jobPosting = jobPosting;
    }

    private void setSimilarityScore(double similarityScore) {
        if (similarityScore < MIN_SIMILARITY_SCORE || similarityScore > MAX_SIMILARITY_SCORE) {
            throw new IllegalArgumentException("Similarity score must be between 0 and 1");
        }

        this.similarityScore = similarityScore;
    }
}
