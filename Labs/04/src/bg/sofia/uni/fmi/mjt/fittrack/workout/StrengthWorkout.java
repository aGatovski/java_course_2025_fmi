package bg.sofia.uni.fmi.mjt.fittrack.workout;

import bg.sofia.uni.fmi.mjt.fittrack.exception.InvalidWorkoutException;

public final class StrengthWorkout implements Workout {
    static final int MIN_DIFFICULTY = 1;
    static final int MAX_DIFFICULTY = 5;

    private String name;
    private int duration;
    private int caloriesBurned;
    private int difficulty;

    public StrengthWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        setName(name);
        setDuration(duration);
        setCaloriesBurned(caloriesBurned);
        setDifficulty(difficulty);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    @Override
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public WorkoutType getType() {
        return WorkoutType.STRENGTH;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidWorkoutException("Name cannot be null or empty!");
        }

        this.name = name;
    }

    public void setDuration(int duration) {
        if (duration <= 0) {
            throw new InvalidWorkoutException("Duration must be a positive number!");
        }

        this.duration = duration;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        if (caloriesBurned <= 0) {
            throw new InvalidWorkoutException("Calories burned must be a positive number");
        }

        this.caloriesBurned = caloriesBurned;
    }

    public void setDifficulty(int difficulty) {
        if (difficulty < MIN_DIFFICULTY || difficulty > MAX_DIFFICULTY) {
            throw new InvalidWorkoutException("Difficulty should be in interval [1,5]");
        }

        this.difficulty = difficulty;
    }
}
