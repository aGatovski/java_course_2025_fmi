package bg.sofia.uni.fmi.mjt.fittrack.workout;

import java.util.Comparator;

public class WorkoutByCaloriesDescComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout workout1, Workout workout2) {
        return Integer.compare(workout2.getCaloriesBurned(), workout1.getCaloriesBurned());
    }
}
