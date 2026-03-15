package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

public record Employer(String companyName, String email) {
    public Employer {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null or blank");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
}
