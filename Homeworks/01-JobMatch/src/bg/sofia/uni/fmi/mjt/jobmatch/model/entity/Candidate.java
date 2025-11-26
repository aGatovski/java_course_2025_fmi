package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.Set;

public class Candidate {
    private String name;
    private String email;
    private Set<Skill> skills;
    private Education education;
    private int yearsOfExperience;

    public Candidate(String name, String email, Set<Skill> skills, Education education, int yearsOfExperience) {
        setName(name);
        setEmail(email);
        setSkills(skills);
        setEducation(education);
        setYearsOfExperience(yearsOfExperience);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public Education getEducation() {
        return education;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        this.name = name;
    }

    private void setEmail(String email) {
        //раздели двете проверки
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        this.email = email;
    }

    private void setSkills(Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skills cannot be null or empty");
        }

        this.skills = skills;
    }

    private void setEducation(Education education) {
        if (education == null) {
            throw new IllegalArgumentException("Education cannot be null");
        }

        this.education = education;
    }

    private void setYearsOfExperience(int yearsOfExperience) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        this.yearsOfExperience = yearsOfExperience;
    }
}
