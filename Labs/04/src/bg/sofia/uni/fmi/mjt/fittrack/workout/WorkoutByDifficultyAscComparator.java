package bg.sofia.uni.fmi.mjt.fittrack.workout;

import java.util.Comparator;

public class WorkoutByDifficultyAscComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout workout1, Workout workout2) {
        return Integer.compare(workout1.getDifficulty(), workout2.getDifficulty());
    }
}
