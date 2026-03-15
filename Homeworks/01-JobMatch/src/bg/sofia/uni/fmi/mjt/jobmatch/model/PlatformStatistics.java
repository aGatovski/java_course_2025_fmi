package bg.sofia.uni.fmi.mjt.jobmatch.model;

public record PlatformStatistics(int totalCandidates, int totalEmployers, int totalJobPostings,
                                 String mostCommonSkillName, String highestPaidJobTitle) {
    public PlatformStatistics {
        if (totalCandidates < 0) {
            throw new IllegalArgumentException("Total candidates number cannot be negative");
        }

        if (totalEmployers < 0) {
            throw new IllegalArgumentException("Total employers number cannot be negative");
        }

        if (totalJobPostings < 0) {
            throw new IllegalArgumentException("Total job postings cannot be negative");
        }

        if (mostCommonSkillName != null && mostCommonSkillName.isBlank()) {
            throw new IllegalArgumentException("Most common skill name cannot be blank");
        }

        if (highestPaidJobTitle != null && highestPaidJobTitle.isBlank()) {
            throw new IllegalArgumentException("Highest paid job title cannot be blank");
        }
    }

}
