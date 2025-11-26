package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CosineSimilarity implements SimilarityStrategy {
    public CosineSimilarity() {
    }

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null || jobSkills == null) {
            throw new IllegalArgumentException("Skill sets cannot be null!");
        }

        if (candidateSkills.isEmpty() && jobSkills.isEmpty()) {
            return 0;
        }

        List<String> combinedSkillsList = getCombinedSkillsList(candidateSkills, jobSkills);
        Collections.sort(combinedSkillsList);

        int[] candidateVector = new int[combinedSkillsList.size()];
        int[] jobVector = new int[combinedSkillsList.size()];
        //Set up the vectors with the skill levels corresponding to the skill at the index
        for (int i = 0; i < combinedSkillsList.size(); i++) {
            String skillName = combinedSkillsList.get(i);
            candidateVector[i] = getSkillLevel(candidateSkills, skillName);
            jobVector[i] = getSkillLevel(jobSkills, skillName);
        }

        return cosineSimilarity(candidateVector, jobVector);
    }

    private int getSkillLevel(Set<Skill> skills, String skillName) {
        for (Skill skill : skills) {
            if (skill.name().equals(skillName)) {
                return skill.level();
            }
        }

        return 0;
    }

    private List<String> getCombinedSkillsList(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        List<String> combinedSkillsList = new ArrayList<>();

        for (Skill skill : candidateSkills) {
            if (!combinedSkillsList.contains(skill.name())) {
                combinedSkillsList.add(skill.name());
            }
        }

        for (Skill skill : jobSkills) {
            if (!combinedSkillsList.contains(skill.name())) {
                combinedSkillsList.add(skill.name());
            }
        }

        return combinedSkillsList;
    }

    private double cosineSimilarity( int[] candidateVector, int[] jobVector) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < candidateVector.length; i++) {
            dotProduct += candidateVector[i] * jobVector[i];
            normA += Math.pow(candidateVector[i], 2);
            normB += Math.pow(jobVector[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
