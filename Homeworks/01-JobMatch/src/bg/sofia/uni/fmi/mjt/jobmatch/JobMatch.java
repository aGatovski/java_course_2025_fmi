package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityScoreFirstCandidateNameSecondDescJobMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityScoreFirstCandidateNameSecondDescSimilarityMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityScoreFirstJobTitleSecondDescJobMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.ImprovementScoreFirstSkillNameSecondDesc;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobMatch implements JobMatchAPI {
    private Map<String, Candidate> candidates;
    private Map<String, Employer> employers;
    private Map<String, JobPosting> jobPostings;

    public JobMatch() {
        this.candidates = new HashMap<>();
        this.employers = new HashMap<>();
        this.jobPostings = new HashMap<>();
    }

    @Override
    public Candidate registerCandidate(Candidate candidate) {
        //validateCandidate() и вътре може да сложиш тези двете
        validateNotNull(candidate, "Candidate to register cannot be null!");
        validateCandidateExists(candidate.getEmail());

        candidates.put(candidate.getEmail(), candidate);
        return candidate;
    }

    @Override
    public Employer registerEmployer(Employer employer) {
        validateNotNull(employer, "Employer to register cannot be null!");
        validateEmployerExists(employer.email());

        employers.put(employer.email(), employer);
        return employer;
    }

    @Override
    public JobPosting postJobPosting(JobPosting jobPosting) {
        validateNotNull(jobPosting, "Job posting to register cannot be null!");
        validateEmployerNotExists(jobPosting.getEmployerEmail());

        jobPostings.put(jobPosting.getId(), jobPosting);
        return jobPosting;
    }

    @Override
    public List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit,
                                                            SimilarityStrategy strategy) {
        validateFindInputs(jobPostingId, limit, strategy, "Job posting ID cannot be null, empty or blank!");
        validateJobPostingNotExists(jobPostingId);

        List<CandidateJobMatch> matchedCandidates = new ArrayList<>();
        Set<Skill> requiredSkills = jobPostings.get(jobPostingId).getRequiredSkills();

        for (Candidate candidate : candidates.values()) {
            double similarityScore = strategy.calculateSimilarity(candidate.getSkills(), requiredSkills);

            if (similarityScore > 0) {
                CandidateJobMatch candidateJobMatch =
                    new CandidateJobMatch(candidate, jobPostings.get(jobPostingId), similarityScore);
                matchedCandidates.add(candidateJobMatch);
            }
        }

        Collections.sort(matchedCandidates, new SimilarityScoreFirstCandidateNameSecondDescJobMatchComparator());

        if (matchedCandidates.size() > limit) {
            matchedCandidates = matchedCandidates.subList(0, limit);
        }

        return Collections.unmodifiableList(matchedCandidates);
    }

    @Override
    public List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail, int limit,
                                                            SimilarityStrategy strategy) {
        validateFindInputs(candidateEmail, limit, strategy, "Candidate email cannot be null, empty or blank!");
        validateCandidateNotExists(candidateEmail);

        Set<Skill> candidateSkills = candidates.get(candidateEmail).getSkills();
        List<CandidateJobMatch> matchedJobs = new ArrayList<>();

        for (JobPosting jobPosting : jobPostings.values()) {
            double similarityScore = strategy.calculateSimilarity(candidateSkills, jobPosting.getRequiredSkills());

            if (similarityScore > 0) {
                CandidateJobMatch candidateJobMatch =
                    new CandidateJobMatch(candidates.get(candidateEmail), jobPosting, similarityScore);
                matchedJobs.add(candidateJobMatch);
            }
        }

        Collections.sort(matchedJobs, new SimilarityScoreFirstJobTitleSecondDescJobMatchComparator());

        if (matchedJobs.size() > limit) {
            matchedJobs = matchedJobs.subList(0, limit);
        }

        return Collections.unmodifiableList(matchedJobs);
    }

    @Override
    public List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit,
                                                                SimilarityStrategy strategy) {
        validateFindInputs(candidateEmail, limit, strategy, "Candidate email cannot be null, empty or blank!");
        validateCandidateNotExists(candidateEmail);

        Set<Skill> candidateSkills = candidates.get(candidateEmail).getSkills();
        List<CandidateSimilarityMatch> matchedCandidates = new ArrayList<>();

        for (Candidate candidate : candidates.values()) {
            if (candidate.getEmail().equals(candidateEmail)) {
                continue;
            }

            double similarityScore = strategy.calculateSimilarity(candidateSkills, candidate.getSkills());

            if (similarityScore > 0) {
                CandidateSimilarityMatch candidateSimilarityMatch =
                    new CandidateSimilarityMatch(candidates.get(candidateEmail), candidate, similarityScore);
                matchedCandidates.add(candidateSimilarityMatch);
            }
        }

        Collections.sort(matchedCandidates, new SimilarityScoreFirstCandidateNameSecondDescSimilarityMatchComparator());

        if (matchedCandidates.size() > limit) {
            matchedCandidates = matchedCandidates.subList(0, limit);
        }

        return Collections.unmodifiableList(matchedCandidates);
    }

    @Override
    public List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail, int limit) {
        SimilarityStrategy defaultStrategy = new CosineSimilarity();
        validateFindInputs(candidateEmail, limit, defaultStrategy, "Candidate email cannot be null, empty or blank!");
        validateCandidateNotExists(candidateEmail);

        Set<Skill> candidateSkills = candidates.get(candidateEmail).getSkills();
        List<SkillRecommendation> skillRecommendations = getSkillRecommendationsList(candidateSkills, defaultStrategy);

        Collections.sort(skillRecommendations, new ImprovementScoreFirstSkillNameSecondDesc());

        if (skillRecommendations.size() > limit) {
            skillRecommendations = skillRecommendations.subList(0, limit);
        }

        return Collections.unmodifiableList(skillRecommendations);
    }

    @Override
    public PlatformStatistics getPlatformStatistics() {
        int totalCandidates = candidates.size();
        int totalEmployers = employers.size();
        int totalJobPostings = jobPostings.size();
        String mostCommonSkillName = null;
        String highestPaidJobTitle = null;

        if (!candidates.isEmpty()) {
            mostCommonSkillName = getMostCommonSkillName();
        }
        if (!jobPostings.isEmpty()) {
            highestPaidJobTitle = getHighestPaidJobTitle();
        }

        return new PlatformStatistics(totalCandidates, totalEmployers, totalJobPostings, mostCommonSkillName,
            highestPaidJobTitle);
    }

    private int getMaxRequiredLevelAcrossAllPostings(String skillName) {
        int maxRequiredLevel = 0;

        for (JobPosting jobPosting : jobPostings.values()) {
            Set<Skill> requiredSkills = jobPosting.getRequiredSkills();

            for (Skill skill : requiredSkills) {
                if (skill.name().equals(skillName)) {
                    if (maxRequiredLevel < skill.level()) {
                        maxRequiredLevel = skill.level();
                    }
                }
            }
        }

        return maxRequiredLevel;
    }

    private void copySkillImprovements(Map<String, Double> fromAggregatedImprovements,
                                       List<SkillRecommendation> toSkillRecommendations) {
        for (String skillName : fromAggregatedImprovements.keySet()) {
            toSkillRecommendations.add(new SkillRecommendation(skillName, fromAggregatedImprovements.get(skillName)));
        }
    }

    private List<SkillRecommendation> getSkillRecommendationsList(Set<Skill> candidateSkills,
                                                                  SimilarityStrategy defaultStrategy) {
        List<SkillRecommendation> skillRecommendations = new ArrayList<>();
        Map<String, Double> aggregatedImprovements = new HashMap<>();

        for (JobPosting jobPosting : jobPostings.values()) {
            Set<Skill> requiredSkills = jobPosting.getRequiredSkills();

            double similarityScore = defaultStrategy.calculateSimilarity(candidateSkills, requiredSkills);

            for (Skill requiredSkill : requiredSkills) {
                if (!candidateSkills.contains(requiredSkill)) {
                    Set<Skill> modifiedCandidateSkills = new HashSet<>(candidateSkills);
                    modifiedCandidateSkills.add(
                        new Skill(requiredSkill.name(), getMaxRequiredLevelAcrossAllPostings(requiredSkill.name())));
                    double recalculatedSimilarityScore =
                        defaultStrategy.calculateSimilarity(modifiedCandidateSkills, requiredSkills);
                    double improvement = recalculatedSimilarityScore - similarityScore;

                    if (aggregatedImprovements.containsKey(requiredSkill.name())) {
                        double currentImprovement = aggregatedImprovements.get(requiredSkill.name());
                        aggregatedImprovements.put(requiredSkill.name(), currentImprovement + improvement);
                    } else {
                        aggregatedImprovements.put(requiredSkill.name(), improvement);
                    }
                }
            }
        }
        copySkillImprovements(aggregatedImprovements, skillRecommendations);
        return skillRecommendations;
    }

    private String getMostCommonSkillName() {
        String mostCommonSkillName = "";
        int mostOccurances = 0;
        Map<String, Integer> skillsNameOccurrences = countSkillFrequencies();

        for (String skillName : skillsNameOccurrences.keySet()) {
            int skillNameOccurrences = skillsNameOccurrences.get(skillName);

            if (skillNameOccurrences > mostOccurances) {
                mostOccurances = skillNameOccurrences;
                mostCommonSkillName = skillName;
            } else if (skillNameOccurrences == mostOccurances) {
                if (mostOccurances > 0 && skillName.compareTo(mostCommonSkillName) < 0) {
                    mostCommonSkillName = skillName;
                }
            }
        }

        return mostCommonSkillName;
    }

    private Map<String, Integer> countSkillFrequencies() {
        Map<String, Integer> skillsNameOccurences = new HashMap<>();

        for (Candidate candidate : candidates.values()) {
            Set<Skill> candidateSkills = candidate.getSkills();

            for (Skill skill : candidateSkills) {
                if (!skillsNameOccurences.containsKey(skill.name())) {
                    skillsNameOccurences.put(skill.name(), 1);
                } else {
                    int occurances = skillsNameOccurences.get(skill.name()) + 1;
                    skillsNameOccurences.put(skill.name(), occurances);
                }
            }
        }

        return skillsNameOccurences;
    }

    private String getHighestPaidJobTitle() {
        double highestPaidJob = 0;
        String highestPaidJobTitle = "";

        for (JobPosting jobPosting : jobPostings.values()) {
            if (jobPosting.getSalary() == highestPaidJob) {
                if (highestPaidJobTitle.compareTo(jobPosting.getTitle()) > 0) {
                    highestPaidJobTitle = jobPosting.getTitle();
                }
            } else if (jobPosting.getSalary() > highestPaidJob) {
                highestPaidJobTitle = jobPosting.getTitle();
            }
        }

        return highestPaidJobTitle;
    }

    private void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateCandidateExists(String email) {
        if (candidates.containsKey(email)) {
            throw new UserAlreadyExistsException("Candidate " + email + " already exists");
        }
    }

    private void validateEmployerExists(String email) {
        if (employers.containsKey(email)) {
            throw new UserAlreadyExistsException("Employer with email " + email + " already exists");
        }
    }

    private void validateEmployerNotExists(String email) {
        if (!employers.containsKey(email)) {
            throw new UserAlreadyExistsException(
                "Employer posting the job is not registered!");
        }
    }

    private void validateString(String input, String message) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateFindInputs(String input, int limit,
                                    SimilarityStrategy strategy, String message) {
        validateString(input, message);

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive!");
        }

        validateNotNull(strategy, "Strategy cannot be null!");
    }

    private void validateJobPostingNotExists(String jobPostingID) {
        if (!jobPostings.containsKey(jobPostingID)) {
            throw new JobPostingNotFoundException("Job posting with this ID does not exist!");
        }
    }

    private void validateCandidateNotExists(String candidateEmail) {
        if (!candidates.containsKey(candidateEmail)) {
            throw new CandidateNotFoundException("Candidate with this email does not exist!");
        }
    }
}
