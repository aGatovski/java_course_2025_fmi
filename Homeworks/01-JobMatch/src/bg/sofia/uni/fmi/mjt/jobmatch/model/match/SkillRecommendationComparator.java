package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import java.util.Comparator;

public class SkillRecommendationComparator implements Comparator<SkillRecommendation> {
    @Override
    public int compare(SkillRecommendation skillRecommendation1, SkillRecommendation skillRecommendation2) {
        //If improvement scores are equal, by candidate name in alphabetical order (case-sensitive)
        if (skillRecommendation1.improvementScore() == skillRecommendation2.improvementScore()) {
            String firstSkillName = skillRecommendation1.skillName();
            String secondSkillName = skillRecommendation2.skillName();
            return firstSkillName.compareTo(secondSkillName);
        }

        // Improvement score in descending order (higher similarity first)
        return Double.compare(skillRecommendation2.improvementScore(), skillRecommendation1.improvementScore());
    }
}
