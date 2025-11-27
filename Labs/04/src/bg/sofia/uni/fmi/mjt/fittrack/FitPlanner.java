package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutByCaloriesDescComparator;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutByCaloriesFirstDifficultySecondDescComparator;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutByDifficultyAscComparator;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitPlanner implements FitPlannerAPI {
    Collection<Workout> availableWorkouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {
        setAvailableWorkouts(availableWorkouts);
    }

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {
        if (filters == null) {
            throw new IllegalArgumentException("Workout filters list is empty!");
        }

        List<Workout> filteredWorkouts = new ArrayList<Workout>();

        for (Workout workout : availableWorkouts) {
            boolean matchAllFilters = true;

            for (WorkoutFilter filter : filters) {
                if (!filter.matches(workout)) { //не пасва 1 чек
                    matchAllFilters = false;
                    break;
                }
            }

            if (matchAllFilters) {
                filteredWorkouts.add(workout); //не съм паднал в капан
            }
        }

        return filteredWorkouts;
    }

    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) throws OptimalPlanImpossibleException {
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("Total minutes must be a positive number");
        }
        if (totalMinutes == 0) {
            return new ArrayList<>();
        }

        if (!checkPlanPossibility(availableWorkouts, totalMinutes)) {
            throw new OptimalPlanImpossibleException("No plan can be generated");
        }

        List<Workout> availableWorkoutsList = new ArrayList<>();
        availableWorkoutsList.addAll(availableWorkouts);

        List<Workout> optimallySelectedWorkouts = getOptimallySelectedWorkouts(totalMinutes, availableWorkoutsList);

        Collections.sort(optimallySelectedWorkouts, new WorkoutByCaloriesFirstDifficultySecondDescComparator());

        return optimallySelectedWorkouts;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {
        EnumMap<WorkoutType, List<Workout>> groupedWorkouts =
            new EnumMap<WorkoutType, List<Workout>>(WorkoutType.class);

        for (WorkoutType workoutType : WorkoutType.values()) {
            groupedWorkouts.put(workoutType, new ArrayList<>());
        }

        for (Workout workout : availableWorkouts) {
            groupedWorkouts.get(workout.getType()).add(workout);
        }

        Map<WorkoutType, List<Workout>> unmodifiedGroupedWorkouts = Collections.unmodifiableMap(groupedWorkouts);

        return unmodifiedGroupedWorkouts;
    }

    @Override
    public List<Workout> getWorkoutsSortedByCalories() {
        List<Workout> sortedByCaloriesWorkouts = new ArrayList<>();
        sortedByCaloriesWorkouts.addAll(availableWorkouts);
        Collections.sort(sortedByCaloriesWorkouts, new WorkoutByCaloriesDescComparator());

        return Collections.unmodifiableList(sortedByCaloriesWorkouts);
    }

    @Override
    public List<Workout> getWorkoutsSortedByDifficulty() {
        List<Workout> sortedByDifficultyWorkouts = new ArrayList<>();
        sortedByDifficultyWorkouts.addAll(availableWorkouts);
        Collections.sort(sortedByDifficultyWorkouts, new WorkoutByDifficultyAscComparator());

        return Collections.unmodifiableList(sortedByDifficultyWorkouts);
    }

    @Override
    public Set<Workout> getUnmodifiableWorkoutSet() {
        Set<Workout> availableWorkoutsSet = new HashSet<>(availableWorkouts);

        return Collections.unmodifiableSet(availableWorkoutsSet);
    }

    private void setAvailableWorkouts(Collection<Workout> availableWorkouts) {
        if (availableWorkouts == null) {
            throw new IllegalArgumentException("Available workouts collection cannot be null.");
        }

        this.availableWorkouts = availableWorkouts;
    }

    // Return the items that were selected
    private List<Workout> getOptimallySelectedWorkouts(int timeCapacity, List<Workout> availableWorkouts) {
        final int availableWorkoutsSize = availableWorkouts.size();
        int[][] dp = new int[availableWorkoutsSize + 1][timeCapacity + 1];

        for (int i = 1; i <= availableWorkoutsSize; i++) {
            int caloriesBurned = availableWorkouts.get(i - 1).getCaloriesBurned();
            int duration = availableWorkouts.get(i - 1).getDuration();

            for (int sz = 1; sz <= timeCapacity; sz++) {
                dp[i][sz] = dp[i - 1][sz];

                if (sz >= duration && dp[i - 1][sz - duration] + caloriesBurned > dp[i][sz]) {
                    dp[i][sz] = dp[i - 1][sz - duration] + caloriesBurned;
                }
            }
        }
        int sz = timeCapacity;
        List<Workout> selectedWorkouts = new ArrayList<>();

        for (int i = availableWorkoutsSize; i > 0; i--) {
            if (dp[i][sz] != dp[i - 1][sz]) {
                int itemIndex = i - 1;
                selectedWorkouts.add(availableWorkouts.get(i - 1));
                sz -= availableWorkouts.get(itemIndex).getDuration();
            }
        }

        return selectedWorkouts;
    }

    private boolean checkPlanPossibility(Collection<Workout> availableWorkouts, int timeCapacity) {
        for (Workout workout : availableWorkouts) {
            if (workout.getDuration() < timeCapacity) {
                return true;
            }
        }

        return false;
    }

}
