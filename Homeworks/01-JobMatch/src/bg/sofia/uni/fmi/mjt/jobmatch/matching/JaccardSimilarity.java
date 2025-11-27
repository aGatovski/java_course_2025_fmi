package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity implements SimilarityStrategy {
    public JaccardSimilarity() {
    }

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        validateSkillsSet(candidateSkills, jobSkills);

        if (candidateSkills.isEmpty() && jobSkills.isEmpty()) {
            return 0;
        }

        Set<String> candidateSkillNames = new HashSet<>();
        for (Skill skill : candidateSkills) {
            candidateSkillNames.add(skill.name());
        }

        Set<String> jobSkillNames = new HashSet<>();
        for (Skill skill : jobSkills) {
            jobSkillNames.add(skill.name());
        }

        Set<String> intersection = new HashSet<>(candidateSkillNames);
        intersection.retainAll(jobSkillNames);

        Set<String> union = new HashSet<>(candidateSkillNames);
        union.addAll(jobSkillNames);

        return (double) intersection.size() / (double) union.size();
    }

    private void validateSkillsSet(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null || jobSkills == null) {
            throw new IllegalArgumentException("Skill sets cannot be null!");
        }
    }
}
