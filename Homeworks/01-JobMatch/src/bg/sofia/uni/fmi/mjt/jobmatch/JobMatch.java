package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatchCandidateNameComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatchJobTitleComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatchComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendationComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobMatch implements JobMatchAPI {
    private final Map<String, Candidate> candidates;
    private final Map<String, Employer> employers;
    private final Map<String, JobPosting> jobPostings;
    private final SimilarityStrategy defaultStrategy;

    public JobMatch() {
        this.candidates = new HashMap<>();
        this.employers = new HashMap<>();
        this.jobPostings = new HashMap<>();
        this.defaultStrategy = new CosineSimilarity();
    }

    @Override
    public Candidate registerCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate to register cannot be null!");
        }

        if (candidates.containsKey(candidate.getEmail())) {
            throw new UserAlreadyExistsException("Candidate " + candidate.getEmail() + " already exists");
        }

        candidates.put(candidate.getEmail(), candidate);
        return candidate;
    }

    @Override
    public Employer registerEmployer(Employer employer) {
        if (employer == null) {
            throw new IllegalArgumentException("Employer to register cannot be null!");
        }

        if (employers.containsKey(employer.email())) {
            throw new UserAlreadyExistsException("Employer with email " + employer.email() + " already exists");
        }

        employers.put(employer.email(), employer);
        return employer;
    }

    @Override
    public JobPosting postJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting to register cannot be null!");
        }

        if (!employers.containsKey(jobPosting.getEmployerEmail())) {
            throw new UserNotFoundException("Employer posting the job is not registered!");
        }

        jobPostings.put(jobPosting.getId(), jobPosting);
        return jobPosting;
    }

    @Override
    public List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit,
                                                            SimilarityStrategy strategy) {
        validateJobMatchInput(jobPostingId, limit, strategy);

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

        Collections.sort(matchedCandidates, new CandidateJobMatchCandidateNameComparator());

        return limitedUnmodiableList(matchedCandidates, limit);
    }

    @Override
    public List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail, int limit,
                                                            SimilarityStrategy strategy) {
        validateCandidateMatchInputs(candidateEmail, limit, strategy);

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

        Collections.sort(matchedJobs, new CandidateJobMatchJobTitleComparator());

        return limitedUnmodiableList(matchedJobs, limit);
    }

    @Override
    public List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit,
                                                                SimilarityStrategy strategy) {
        validateCandidateMatchInputs(candidateEmail, limit, strategy);

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

        Collections.sort(matchedCandidates, new CandidateSimilarityMatchComparator());

        return limitedUnmodiableList(matchedCandidates, limit);
    }

    @Override
    public List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail, int limit) {
        validateCandidateMatchInputs(candidateEmail, limit, defaultStrategy);

        Set<Skill> candidateSkills = candidates.get(candidateEmail).getSkills();
        List<SkillRecommendation> skillRecommendations = getSkillRecommendationsList(candidateSkills, defaultStrategy);

        Collections.sort(skillRecommendations, new SkillRecommendationComparator());
        //4. Return top N skills ranked by total improvement potential
        return limitedUnmodiableList(skillRecommendations, limit);
    }

    @Override
    public PlatformStatistics getPlatformStatistics() {
        return new PlatformStatistics(candidates.size(), employers.size(), jobPostings.size(), getMostCommonSkillName(),
            getHighestPaidJobTitle());
    }

    private void validateJobMatchInput(String jobPostingId, int limit, SimilarityStrategy strategy) {
        if (jobPostingId == null || jobPostingId.isBlank()) {
            throw new IllegalArgumentException("Job posting ID cannot be null or blank!");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive!");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null!");
        }

        if (!jobPostings.containsKey(jobPostingId)) {
            throw new JobPostingNotFoundException("Job posting with this ID does not exist!");
        }
    }

    private void validateCandidateMatchInputs(String candidateEmail, int limit, SimilarityStrategy strategy) {
        if (candidateEmail == null || candidateEmail.isBlank()) {
            throw new IllegalArgumentException("Candidate email cannot be null or blank!");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive!");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null!");
        }

        if (!candidates.containsKey(candidateEmail)) {
            throw new CandidateNotFoundException("Candidate with this email does not exist!");
        }
    }

    private <T> List<T> limitedUnmodiableList(List<T> list, int limit) {
        if (list.size() > limit) {
            return Collections.unmodifiableList(list.subList(0, limit));
        }

        return Collections.unmodifiableList(list);
    }

    private List<SkillRecommendation> getSkillRecommendationsList(Set<Skill> candidateSkills,
                                                                  SimilarityStrategy defaultStrategy) {
        List<SkillRecommendation> skillRecommendations = new ArrayList<>();
        Map<String, Double> aggregatedImprovements = aggregateSkillImprovements(candidateSkills, defaultStrategy);

        for (String skillName : aggregatedImprovements.keySet()) {
            skillRecommendations.add(new SkillRecommendation(skillName, aggregatedImprovements.get(skillName)));
        }

        return skillRecommendations;
    }

    //This class combines all missing skills at the highest required level
    //3. Aggregate (sum up) improvements across all job postings for each missing skill
    private Map<String, Double> aggregateSkillImprovements(Set<Skill> candidateSkills,
                                                           SimilarityStrategy defaultStrategy) {
        Map<String, Double> aggregatedImprovements = new HashMap<>();
        //For each job posting, calculate current similarity score with the candidate
        for (JobPosting jobPosting : jobPostings.values()) {
            double similarityScore =
                defaultStrategy.calculateSimilarity(candidateSkills, jobPosting.getRequiredSkills());

            processSkillImprovements(candidateSkills, jobPosting.getRequiredSkills(), defaultStrategy, similarityScore,
                aggregatedImprovements);
        }

        return aggregatedImprovements;
    }

    private void processSkillImprovements(Set<Skill> candidateSkills, Set<Skill> requiredSkills,
                                          SimilarityStrategy defaultStrategy,
                                          double currentSimilarityScore,
                                          Map<String, Double> aggregatedImprovements) {
        for (Skill requiredSkill : requiredSkills) {
            //For each skill the candidate is MISSING (present in job but not in candidate profile):
            if (!candidateSkills.contains(requiredSkill)) {
                // Temporarily add that skill to candidate's profile with level equal to the max. required
                Set<Skill> modifiedCandidateSkills = new HashSet<>(candidateSkills);
                modifiedCandidateSkills.add(
                    new Skill(requiredSkill.name(), getMaxRequiredLevelAcrossAllPostings(requiredSkill.name())));

                //Recalculate similarity score
                double recalculatedSimilarityScore =
                    defaultStrategy.calculateSimilarity(modifiedCandidateSkills, requiredSkills);

                //Calculate improvement: new_score - old_score
                double improvement = recalculatedSimilarityScore - currentSimilarityScore;

                if (aggregatedImprovements.containsKey(requiredSkill.name())) {
                    double currentImprovement = aggregatedImprovements.get(requiredSkill.name());
                    aggregatedImprovements.put(requiredSkill.name(), currentImprovement + improvement);
                } else {
                    aggregatedImprovements.put(requiredSkill.name(), improvement);
                }
            }
        }
    }

    private int getMaxRequiredLevelAcrossAllPostings(String skillName) {
        int maxRequiredLevel = 0;

        for (JobPosting jobPosting : jobPostings.values()) {
            for (Skill skill : jobPosting.getRequiredSkills()) {
                if (skill.name().equals(skillName) && skill.level() > maxRequiredLevel) {
                    maxRequiredLevel = skill.level();
                }
            }
        }

        return maxRequiredLevel;
    }

    private String getMostCommonSkillName() {
        if (candidates.isEmpty()) {
            return null;
        }

        Map<String, Integer> skillsNameOccurrences = countSkillFrequencies();

        return findMostCommonSkill(skillsNameOccurrences);
    }

    private Map<String, Integer> countSkillFrequencies() {
        Map<String, Integer> skillsNameOccurences = new HashMap<>();

        for (Candidate candidate : candidates.values()) {
            Set<Skill> candidateSkills = candidate.getSkills();

            for (Skill skill : candidateSkills) {
                String skillName = skill.name();

                if (skillsNameOccurences.containsKey(skillName)) {
                    skillsNameOccurences.put(skillName, skillsNameOccurences.get(skillName) + 1);
                } else {
                    skillsNameOccurences.put(skill.name(), 1);
                }
            }
        }

        return skillsNameOccurences;
    }

    private String findMostCommonSkill(Map<String, Integer> skillsNameOccurences) {
        String mostCommonSkillName = "";
        int mostOccurances = 0;

        for (String skillName : skillsNameOccurences.keySet()) {
            int skillNameOccurrences = skillsNameOccurences.get(skillName);

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

    private String getHighestPaidJobTitle() {
        if (jobPostings.isEmpty()) {
            return null;
        }

        double highestPaidJob = -1;
        String highestPaidJobTitle = "";

        for (JobPosting jobPosting : jobPostings.values()) {
            double currentSalary = jobPosting.getSalary();

            if (currentSalary > highestPaidJob) {
                highestPaidJob = currentSalary;
                highestPaidJobTitle = jobPosting.getTitle();
            } else if (currentSalary == highestPaidJob) {
                if (highestPaidJobTitle.compareTo(jobPosting.getTitle()) > 0) {
                    highestPaidJobTitle = jobPosting.getTitle();
                }
            }
        }

        return highestPaidJobTitle;
    }
}
