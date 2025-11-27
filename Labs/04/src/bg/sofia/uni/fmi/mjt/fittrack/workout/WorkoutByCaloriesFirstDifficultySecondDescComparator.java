package bg.sofia.uni.fmi.mjt.fittrack.workout;

import java.util.Comparator;

public class WorkoutByCaloriesFirstDifficultySecondDescComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout workout1, Workout workout2) {
        if (workout1.getCaloriesBurned() == workout2.getCaloriesBurned()) {
            return Integer.compare(workout2.getDifficulty(), workout1.getDifficulty());
        }
        return Integer.compare(workout2.getCaloriesBurned(), workout1.getCaloriesBurned());
    }
}
