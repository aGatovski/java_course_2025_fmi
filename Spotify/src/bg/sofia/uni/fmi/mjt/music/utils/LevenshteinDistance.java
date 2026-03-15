package bg.sofia.uni.fmi.mjt.music.utils;

public class LevenshteinDistance {

    public static int calculate(String s1, String s2) {
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Strings cannot be null");
        }

        String a = s1.toLowerCase();
        String b = s2.toLowerCase();

        int[] prevRow = new int[b.length() + 1];
        int[] currRow = new int[b.length() + 1];

        for (int j = 0; j <= b.length(); j++) {
            prevRow[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            currRow[0] = i;

            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                currRow[j] = Math.min(Math.min(prevRow[j] + 1, currRow[j - 1] + 1), prevRow[j - 1] + cost);
            }

            int[] temp = prevRow;
            prevRow = currRow;
            currRow = temp;
        }

        return prevRow[b.length()];
    }

    public static double similarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());

        if (maxLen == 0) {
            return 1.0;
        }

        int distance = calculate(s1, s2);
        return 1 - ((double) distance / maxLen);
    }
}
