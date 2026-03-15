package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

public record SkillRecommendation(String skillName, double improvementScore) {
    public SkillRecommendation {
        if (improvementScore < 0) {
            throw new IllegalArgumentException("Improvement score cannot be negative");
        }

        if (skillName == null || skillName.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank");
        }
    }
}
