
public class UniqueSubstringFinder {
    public static String longestUniqueSubstring(String s) {

        if (s == null || s.isEmpty()) return ""; //подобрение въпреки че кода ще го оправи

        boolean[] seen = new boolean[26]; //false ако не се среща true ако се , false по подразбиране
        int start = 0, end = 0, maxStart = 0, maxEnd = 0, maxLength = 0;

        while (end != s.length()) {
            if (seen[s.charAt(end) - 'a']) {  //sliding window
                while (seen[s.charAt(end) - 'a']) {
                    seen[s.charAt(start) - 'a'] = false;
                    start++;
                }
            }

            seen[s.charAt(end) - 'a'] = true;
            end++;

            if (end - start > maxLength) {
                maxEnd = end;
                maxStart = start;
                maxLength = maxEnd - maxStart;
            }
        }

        return s.substring(maxStart, maxEnd);
    }
};


