package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.HashSet;
import java.util.Set;

public class JobPosting {
    private String id;
    private String title;
    private String employerEmail;
    private Set<Skill> requiredSkills;
    private Education requiredEducation;
    private int requiredYearsOfExperience;
    private double salary;

    public JobPosting(String id, String title, String employerEmail, Set<Skill> requiredSkills,
                      Education requiredEducation,
                      int requiredYearsOfExperience, double salary) {
        setId(id);
        setTitle(title);
        setEmployerEmail(employerEmail);
        setRequiredSkills(requiredSkills);
        setRequiredEducation(requiredEducation);
        setRequiredYearsOfExperience(requiredYearsOfExperience);
        setSalary(salary);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public Set<Skill> getRequiredSkills() {
        return new HashSet<>(requiredSkills);
    }

    public Education getRequiredEducation() {
        return requiredEducation;
    }

    public int getRequiredYearsOfExperience() {
        return requiredYearsOfExperience;
    }

    public double getSalary() {
        return salary;
    }

    private void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }

        this.id = id;
    }

    private void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }

        this.title = title;
    }

    private void setEmployerEmail(String employerEmail) {
        if (employerEmail == null || employerEmail.isBlank()) {
            throw new IllegalArgumentException("Employer email cannot be null or blank");
        }

        this.employerEmail = employerEmail;
    }

    private void setRequiredSkills(Set<Skill> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new IllegalArgumentException("Required skills cannot be null or empty");
        }

        this.requiredSkills = new HashSet<>(requiredSkills);
    }

    private void setRequiredEducation(Education requiredEducation) {
        if (requiredEducation == null) {
            throw new IllegalArgumentException("Required education cannot be null");
        }

        this.requiredEducation = requiredEducation;
    }

    private void setRequiredYearsOfExperience(int requiredYearsOfExperience) {
        if (requiredYearsOfExperience < 0) {
            throw new IllegalArgumentException("Required years of experience cannot be negative");
        }

        this.requiredYearsOfExperience = requiredYearsOfExperience;
    }

    private void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }

        this.salary = salary;
    }
}
