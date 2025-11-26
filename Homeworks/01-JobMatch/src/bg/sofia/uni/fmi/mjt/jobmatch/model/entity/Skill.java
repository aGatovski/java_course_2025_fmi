package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

public record Skill(String name, int level) {
    private static final int MIN_EXPERIENCE = 0;
    private static final int MAX_EXPERIENCE = 5;

    public Skill {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank");
        }

        if (level < MIN_EXPERIENCE || level > MAX_EXPERIENCE) {
            throw new IllegalArgumentException("Skill level must be between 0 and 5");
        }
    }
}
