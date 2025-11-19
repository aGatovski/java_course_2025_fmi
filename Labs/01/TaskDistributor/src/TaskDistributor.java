public class TaskDistributor {
    public static int minDifference(int[] tasks) {
        int bestSubset = 0, remainingSubset = 0, totalTime = 0;

        if (tasks.length == 0) { // 3 edge cases for now
            return 0;
        } else if (tasks.length == 1) {
            return tasks[0];
        }

        for (int task : tasks) {
            totalTime += task;
        }

        int halfTime = totalTime / 2;

        boolean[] dp = new boolean[halfTime + 1]; // false by default
        dp[0] = true;
        for (int task : tasks) {
            if (task > halfTime) continue;
            for (int i = halfTime; i >= task; i--) {
                if (dp[i - task]) {
                    dp[i] = true;
                }
            }
        }

        for (int i = dp.length - 1; i >= 0; i--) {
            if (dp[i]) {
                bestSubset = i;
                break;
            }
        }
        remainingSubset = totalTime - bestSubset;
        return Math.abs(bestSubset - remainingSubset);

    }
}
