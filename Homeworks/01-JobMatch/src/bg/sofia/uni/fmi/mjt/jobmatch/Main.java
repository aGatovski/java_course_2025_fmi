package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
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

public class Main {

    public static void main(String[] args) {
        // 1. Инициализация на системата
        JobMatchAPI jobMatch = new JobMatch();
        SimilarityStrategy cosineStrategy = new CosineSimilarity();

        System.out.println("--- 1. REGISTERING USERS ---");

        // Създаване на работодател
        Employer google = new Employer("Google", "hr@google.com");
        Employer startUp = new Employer("NextGen AI", "jobs@ai.com");

        jobMatch.registerEmployer(google);
        jobMatch.registerEmployer(startUp);
        System.out.println("Employers registered.");

        // Създаване на кандидати
        // Кандидат 1: Java Expert
        Set<Skill> aliceSkills = new HashSet<>();
        aliceSkills.add(new Skill("Java", 5));
        aliceSkills.add(new Skill("SQL", 4));
        aliceSkills.add(new Skill("Spring", 5));
        Candidate alice = new Candidate("Alice", "alice@mail.com", aliceSkills, Education.MASTERS, 5);

        // Кандидат 2: Python/AI Expert
        Set<Skill> bobSkills = new HashSet<>();
        bobSkills.add(new Skill("Python", 5));
        bobSkills.add(new Skill("TensorFlow", 4));
        bobSkills.add(new Skill("SQL", 3));
        Candidate bob = new Candidate("Bob", "bob@mail.com", bobSkills, Education.PHD, 3);

        // Кандидат 3: Junior (знае малко Java, но няма SQL)
        Set<Skill> charlieSkills = new HashSet<>();
        charlieSkills.add(new Skill("Java", 2));
        charlieSkills.add(new Skill("HTML", 3));
        Candidate charlie = new Candidate("Charlie", "charlie@mail.com", charlieSkills, Education.BACHELORS, 0);

        jobMatch.registerCandidate(alice);
        jobMatch.registerCandidate(bob);
        jobMatch.registerCandidate(charlie);
        System.out.println("Candidates registered.");

        System.out.println("\n--- 2. POSTING JOBS ---");

        // Обява 1: Java Backend (Google)
        Set<Skill> javaJobSkills = new HashSet<>();
        javaJobSkills.add(new Skill("Java", 5));
        javaJobSkills.add(new Skill("SQL", 4));
        javaJobSkills.add(new Skill("Spring", 4));

        JobPosting javaJob = new JobPosting(
            "JOB-1",
            "Senior Java Developer",
            "hr@google.com",
            javaJobSkills,
            Education.BACHELORS,
            3,
            120000
        );

        // Обява 2: Data Scientist (NextGen AI)
        Set<Skill> dataJobSkills = new HashSet<>();
        dataJobSkills.add(new Skill("Python", 5));
        dataJobSkills.add(new Skill("SQL", 4));
        dataJobSkills.add(new Skill("TensorFlow", 5));

        JobPosting dataJob = new JobPosting(
            "JOB-2",
            "Lead Data Scientist",
            "jobs@ai.com",
            dataJobSkills,
            Education.MASTERS,
            5,
            150000
        );

        jobMatch.postJobPosting(javaJob);
        jobMatch.postJobPosting(dataJob);
        System.out.println("Jobs posted.");

        System.out.println("\n--- 3. FINDING CANDIDATES FOR JOB (Java Developer) ---");
        // Очакваме Alice да е първа (високо сходство), Charlie може да е втори (ниско), Bob вероятно 0
        List<CandidateJobMatch> matches = jobMatch.findTopNCandidatesForJob("JOB-1", 5, cosineStrategy);

        for (CandidateJobMatch match : matches) {
            System.out.printf("Candidate: %-10s | Score: %.4f%n",
                match.getCandidate().getName(),
                match.getSimilarityScore()
            );
        }

        System.out.println("\n--- 4. FINDING JOBS FOR CANDIDATE (Bob) ---");
        // Очакваме Data Scientist обявата
        List<CandidateJobMatch> bobMatches = jobMatch.findTopNJobsForCandidate("bob@mail.com", 5, cosineStrategy);

        for (CandidateJobMatch match : bobMatches) {
            System.out.printf("Job: %-20s | Score: %.4f%n",
                match.getJobPosting().getTitle(), match.getSimilarityScore());
        }

        System.out.println("\n--- 5. SKILL RECOMMENDATIONS (For Charlie) ---");
        // Charlie знае Java:2. JavaJob иска SQL:4 и Java:5. DataJob иска Python:5.
        // Очакваме препоръка за SQL или Spring, защото те ще вдигнат резултата му за Java обявата.
        List<SkillRecommendation> recommendations = jobMatch.getSkillRecommendationsForCandidate("charlie@mail.com", 3);

        for (SkillRecommendation rec : recommendations) {
            System.out.printf("Learn Skill: %-10s | Improvement: %.4f%n",
                rec.skillName(), rec.improvementScore());
        }

        System.out.println("\n--- 6. SIMILAR CANDIDATES (To Alice) ---");
        // Търсим кой прилича на Alice. Charlie знае малко Java, така че може да има леко сходство.
        List<CandidateSimilarityMatch> similar = jobMatch.findSimilarCandidates("alice@mail.com", 3, cosineStrategy);

        if (similar.isEmpty()) {
            System.out.println("No similar candidates found.");
        } else {
            for (CandidateSimilarityMatch match : similar) {
                System.out.printf("Similar to: %-10s | Score: %.4f%n",
                    match.getSimilarCandidate().getName(), match.getSimilarityScore());
            }
        }

        System.out.println("\n--- 7. PLATFORM STATISTICS ---");
        PlatformStatistics stats = jobMatch.getPlatformStatistics();
        System.out.println("Total Candidates: " + stats.totalCandidates());
        System.out.println("Total Jobs: " + stats.totalJobPostings());
        System.out.println("Most Common Skill: " + stats.mostCommonSkillName()); // Очакваме SQL (има го Alice, Bob, JavaJob, DataJob)
        System.out.println("Highest Paid Job: " + stats.highestPaidJobTitle());
    }
}