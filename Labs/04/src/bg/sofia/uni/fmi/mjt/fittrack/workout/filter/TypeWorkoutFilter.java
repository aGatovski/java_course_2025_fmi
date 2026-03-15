package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;

public class TypeWorkoutFilter implements WorkoutFilter {
    private WorkoutType type;

    public TypeWorkoutFilter(WorkoutType type) {
        setType(type);
    }

    @Override
    public boolean matches(Workout workout) {
        return workout.getType() == type;
    }

    public void setType(WorkoutType type) {
        if (type == null) {
            throw new IllegalArgumentException("Workout type cannot be null.");
        }

        this.type = type;
    }
}
