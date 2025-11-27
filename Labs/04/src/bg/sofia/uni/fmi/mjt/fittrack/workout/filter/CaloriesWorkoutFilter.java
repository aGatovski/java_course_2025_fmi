package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class CaloriesWorkoutFilter implements WorkoutFilter {
    private int min;
    private int max;

    public CaloriesWorkoutFilter(int min, int max) {
        setMax(max);
        setMin(min);

        if (min > max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
    }

    @Override
    public boolean matches(Workout workout) {
        return min <= workout.getCaloriesBurned() && workout.getCaloriesBurned() <= max;
    }

    public void setMin(int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Min must be positive");
        }

        this.min = min;
    }

    public void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Max must be positive integer!");
        }

        this.max = max;
    }
}
