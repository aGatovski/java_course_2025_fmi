package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.JaccardSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Education;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobMatchTest {

    public static void main(String[] args) {
        JobMatchTest test = new JobMatchTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("Running JobMatch Tests...\n");

        testCandidateRegistration();
        testEmployerRegistration();
        testJobPostingCreation();
        testFindTopNCandidatesForJob();
        testFindTopNJobsForCandidate();
        testFindSimilarCandidates();
        testGetSkillRecommendations();
        testPlatformStatistics();
        testEdgeCases();
        testExceptionHandling();

        System.out.println("\nAll tests completed!");
    }

    private void testCandidateRegistration() {
        System.out.println("Testing Candidate Registration...");
        JobMatch jobMatch = new JobMatch();

        Set<Skill> skills = new HashSet<>();
        skills.add(new Skill("Java", 4));
        skills.add(new Skill("Python", 3));

        Candidate candidate = new Candidate("John Doe", "john@example.com", skills, Education.BACHELORS, 3);

        Candidate registered = jobMatch.registerCandidate(candidate);
        assert registered.equals(candidate) : "Registered candidate should be the same instance";

        try {
            jobMatch.registerCandidate(candidate);
            assert false : "Should throw UserAlreadyExistsException";
        } catch (UserAlreadyExistsException e) {
            // Expected
        }

        System.out.println("✓ Candidate registration tests passed");
    }

    private void testEmployerRegistration() {
        System.out.println("Testing Employer Registration...");
        JobMatch jobMatch = new JobMatch();

        Employer employer = new Employer("Tech Corp", "hr@techcorp.com");

        Employer registered = jobMatch.registerEmployer(employer);
        assert registered.equals(employer) : "Registered employer should be the same instance";

        try {
            jobMatch.registerEmployer(employer);
            assert false : "Should throw UserAlreadyExistsException";
        } catch (UserAlreadyExistsException e) {
            // Expected
        }

        System.out.println("✓ Employer registration tests passed");
    }

    private void testJobPostingCreation() {
        System.out.println("Testing Job Posting Creation...");
        JobMatch jobMatch = new JobMatch();

        Employer employer = new Employer("Tech Corp", "hr@techcorp.com");
        jobMatch.registerEmployer(employer);

        Set<Skill> requiredSkills = new HashSet<>();
        requiredSkills.add(new Skill("Java", 5));
        requiredSkills.add(new Skill("SQL", 3));

        JobPosting jobPosting = new JobPosting("JOB001", "Senior Developer",
            "hr@techcorp.com", requiredSkills,
            Education.MASTERS, 5, 80000.0);

        JobPosting posted = jobMatch.postJobPosting(jobPosting);
        assert posted.equals(jobPosting) : "Posted job should be the same instance";

        try {
            JobPosting invalidJob = new JobPosting("JOB002", "Junior Developer",
                "unknown@company.com", requiredSkills,
                Education.BACHELORS, 2, 50000.0);
            jobMatch.postJobPosting(invalidJob);
            assert false : "Should throw UserNotFoundException";
        } catch (UserNotFoundException e) {
            // Expected
        }

        System.out.println("✓ Job posting creation tests passed");
    }

    private void testFindTopNCandidatesForJob() {
        System.out.println("Testing Find Top N Candidates for Job...");
        JobMatch jobMatch = setupJobMatchWithData();

        List<CandidateJobMatch> matches = jobMatch.findTopNCandidatesForJob(
            "JOB001", 2, new CosineSimilarity());

        assert matches.size() <= 2 : "Should return at most 2 matches";
        assert !matches.isEmpty() : "Should find at least one match";

        // Test sorting by similarity score (descending)
        if (matches.size() > 1) {
            assert matches.get(0).getSimilarityScore() >= matches.get(1).getSimilarityScore()
                : "Results should be sorted by similarity score descending";
        }

        System.out.println("✓ Find top candidates tests passed");
    }

    private void testFindTopNJobsForCandidate() {
        System.out.println("Testing Find Top N Jobs for Candidate...");
        JobMatch jobMatch = setupJobMatchWithData();

        List<CandidateJobMatch> matches = jobMatch.findTopNJobsForCandidate(
            "john@example.com", 3, new JaccardSimilarity());

        assert matches.size() <= 3 : "Should return at most 3 matches";

        System.out.println("✓ Find top jobs tests passed");
    }

    private void testFindSimilarCandidates() {
        System.out.println("Testing Find Similar Candidates...");
        JobMatch jobMatch = setupJobMatchWithData();

        List<CandidateSimilarityMatch> matches = jobMatch.findSimilarCandidates(
            "john@example.com", 2, new CosineSimilarity());

        assert matches.size() <= 2 : "Should return at most 2 matches";

        // Verify target candidate is not included in results
        for (CandidateSimilarityMatch match : matches) {
            assert !match.getSimilarCandidate().getEmail().equals("john@example.com")
                : "Target candidate should not be in results";
        }

        System.out.println("✓ Find similar candidates tests passed");
    }

    private void testGetSkillRecommendations() {
        System.out.println("Testing Skill Recommendations...");
        JobMatch jobMatch = setupJobMatchWithData();

        List<SkillRecommendation> recommendations = jobMatch.getSkillRecommendationsForCandidate(
            "john@example.com", 3);

        assert recommendations.size() <= 3 : "Should return at most 3 recommendations";

        // Test sorting by improvement score (descending)
        if (recommendations.size() > 1) {
            assert recommendations.get(0).improvementScore() >= recommendations.get(1).improvementScore()
                : "Results should be sorted by improvement score descending";
        }

        System.out.println("✓ Skill recommendations tests passed");
    }

    private void testPlatformStatistics() {
        System.out.println("Testing Platform Statistics...");
        JobMatch jobMatch = setupJobMatchWithData();

        PlatformStatistics stats = jobMatch.getPlatformStatistics();

        assert stats.totalCandidates() >= 0 : "Total candidates should be non-negative";
        assert stats.totalEmployers() >= 0 : "Total employers should be non-negative";
        assert stats.totalJobPostings() >= 0 : "Total job postings should be non-negative";

        System.out.println("✓ Platform statistics tests passed");
    }

    private void testEdgeCases() {
        System.out.println("Testing Edge Cases...");
        JobMatch jobMatch = new JobMatch();

        // Empty platform statistics
        PlatformStatistics emptyStats = jobMatch.getPlatformStatistics();
        assert emptyStats.totalCandidates() == 0 : "Should have 0 candidates initially";
        assert emptyStats.totalEmployers() == 0 : "Should have 0 employers initially";
        assert emptyStats.totalJobPostings() == 0 : "Should have 0 job postings initially";
        assert emptyStats.mostCommonSkillName() == null : "Most common skill should be null when no candidates";
        assert emptyStats.highestPaidJobTitle() == null : "Highest paid job should be null when no jobs";

        System.out.println("✓ Edge case tests passed");
    }

    private void testExceptionHandling() {
        System.out.println("Testing Exception Handling...");
        JobMatch jobMatch = new JobMatch();

        // Test null candidate registration
        try {
            jobMatch.registerCandidate(null);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Test null employer registration
        try {
            jobMatch.registerEmployer(null);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Test candidate not found
        try {
            jobMatch.findTopNJobsForCandidate("nonexistent@email.com", 5, new CosineSimilarity());
            assert false : "Should throw CandidateNotFoundException";
        } catch (CandidateNotFoundException e) {
            // Expected
        }

        // Test job posting not found
        try {
            jobMatch.findTopNCandidatesForJob("NONEXISTENT", 5, new CosineSimilarity());
            assert false : "Should throw JobPostingNotFoundException";
        } catch (JobPostingNotFoundException e) {
            // Expected
        }

        System.out.println("✓ Exception handling tests passed");
    }

    private JobMatch setupJobMatchWithData() {
        JobMatch jobMatch = new JobMatch();

        // Register employers
        jobMatch.registerEmployer(new Employer("Tech Corp", "hr@techcorp.com"));
        jobMatch.registerEmployer(new Employer("Data Inc", "jobs@datainc.com"));

        // Register candidates
        Set<Skill> johnSkills = new HashSet<>();
        johnSkills.add(new Skill("Java", 4));
        johnSkills.add(new Skill("Python", 3));
        johnSkills.add(new Skill("SQL", 2));
        jobMatch.registerCandidate(new Candidate("John Doe", "john@example.com",
            johnSkills, Education.BACHELORS, 3));

        Set<Skill> janeSkills = new HashSet<>();
        janeSkills.add(new Skill("Java", 5));
        janeSkills.add(new Skill("AWS", 4));
        janeSkills.add(new Skill("Docker", 3));
        jobMatch.registerCandidate(new Candidate("Jane Smith", "jane@example.com",
            janeSkills, Education.MASTERS, 5));

        Set<Skill> bobSkills = new HashSet<>();
        bobSkills.add(new Skill("Python", 4));
        bobSkills.add(new Skill("SQL", 5));
        jobMatch.registerCandidate(new Candidate("Bob Johnson", "bob@example.com",
            bobSkills, Education.PHD, 7));

        // Post job postings
        Set<Skill> job1Skills = new HashSet<>();
        job1Skills.add(new Skill("Java", 5));
        job1Skills.add(new Skill("SQL", 3));
        job1Skills.add(new Skill("AWS", 3));
        jobMatch.postJobPosting(new JobPosting("JOB001", "Senior Developer", "hr@techcorp.com",
            job1Skills, Education.MASTERS, 5, 80000.0));

        Set<Skill> job2Skills = new HashSet<>();
        job2Skills.add(new Skill("Python", 4));
        job2Skills.add(new Skill("SQL", 4));
        job2Skills.add(new Skill("Docker", 2));
        jobMatch.postJobPosting(new JobPosting("JOB002", "Data Scientist", "jobs@datainc.com",
            job2Skills, Education.PHD, 3, 75000.0));

        return jobMatch;
    }
}