package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class NameWorkoutFilter implements WorkoutFilter {
    private String keyword;
    private boolean caseSensitive;

    public NameWorkoutFilter(String keyword, boolean caseSensitive) {
        setKeyword(keyword);
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean matches(Workout workout) {
        if (caseSensitive) {
            return workout.getName().contains(keyword);
        }

        return workout.getName().toLowerCase().contains(keyword.toLowerCase());
    }

    public void setKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("Keyword must not be null or empty string!");
        }

        this.keyword = keyword;
    }
}
