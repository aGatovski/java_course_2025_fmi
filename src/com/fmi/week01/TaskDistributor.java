package com.fmi.week01;

public class TaskDistributor {
    public static int minDifference(int[] tasks){
        int bestSubset = 0 , remainingSubset = 0, totalTime = 0;

        if(tasks.length == 0){ // 3 edge cases for now
            return 0;
        } else if(tasks.length == 1){
            return tasks[0];
        } else if(tasks.length == 2){
            return Math.abs(bestSubset - remainingSubset);
        }

        for (int task : tasks){
            totalTime += task;
        }

        int halfTime = totalTime /2;

        boolean[] dp = new boolean[halfTime + 1]; // false by default
        dp[0] = true;
        for (int task : tasks){
            if(task > halfTime) continue;
            for(int i = halfTime; i>= task ; i--){
                if(dp[i - task]){ // ако dp[i - task] е true това означава че dp[i] ще е true защото е достижимо чрез dp[i - task] + task
                    dp[i] = true;
                }
            }
        }

        for(int i = dp.length - 1; i >= 0; i--){
            if(dp[i]){
               bestSubset = i;
               break;
            }
        }
        remainingSubset = totalTime - bestSubset;
        return Math.abs(bestSubset - remainingSubset);

    }


    public static void main(String[] args) {
        System.out.println(minDifference(new int[]{1, 2, 3, 4, 5})); // 1
        System.out.println(minDifference(new int[]{10, 20, 15, 5})); // 0
        System.out.println(minDifference(new int[]{7, 3, 2, 1, 5, 4})); // 0
        System.out.println(minDifference(new int[]{9, 1, 1, 1})); // 6
        System.out.println(minDifference(new int[]{})); // 0
        System.out.println(minDifference(new int[]{120})); // 120
        System.out.println(minDifference(new int[]{30, 30})); // 0
    }
}
