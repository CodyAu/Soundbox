package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Calculate {

    /**
     * Implementing the Levenshtein distance formula from https://en.wikipedia.org/wiki/Levenshtein_distance
     *
     * @param str1
     * @param str2
     * @param substring Whether to match the whole string or to look for the lowest substring match.
     * @return Levenshtein distance
     */
    public static int levenshtein(@NonNull String str1, @NonNull String str2, boolean substring) {
        final int m = str1.length();
        final int n = str2.length();

        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        int[] costs = new int[n + 1];
        int[] tempCosts = new int[n + 1];
        if(!substring){ // If calculating the distance on the whole string must initialize the first array of the matrix to [0, 1, 2, 3, ..., n]
            for (int j = 0; j < costs.length; j++)
                costs[j] = j;
        }

        if (m <= 0) return n;
        if (n <= 0) return m;

        for (int i = 1; i <= m; i++) {
            tempCosts[0] = i;
            for (int j = 1; j <= n; j++) {
                tempCosts[j] = str1.charAt(i - 1) == str2.charAt(j - 1) ? costs[j - 1] : Math.min(costs[j - 1], Math.min(costs[j], tempCosts[j - 1])) + 1;
            }
            System.arraycopy(tempCosts, 0, costs, 0, n + 1);
        }

        int finalCost = costs[n];
        if(substring){ // If calculating substring costs, find the minimum from the final row of the calculated matrix.
            for(int i = n - 1; i >= 0; i--){
                if(costs[i] < finalCost) finalCost = costs[i];
            }
        }
        return finalCost;
    }

    public static int levenshtein(@NonNull String str1, @NonNull String str2){
        return levenshtein(str1, str2, false);
    }

    /**
     * @param string
     * @param strings
     * @param substring Whether to match the whole string or to look for the lowest substring match.
     * @return Levenshtein distances for each element
     */
    public static int[] levenshteins(String string, List<String> strings, boolean substring) {
        if (strings == null || string == null || strings.isEmpty()) return null;

        final int[] levs = new int[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            levs[i] = levenshtein(string, strings.get(i), substring);
        }

        return levs;
    }

    public static int[] levenshteins(String string, List<String> strings) {
        return levenshteins(string, strings, false);
    }

    /**
     *
     * @param string
     * @param strings
     * @param substring Whether to match the whole string or to look for the lowest substring match.
     * @return Closest levenshtein distance match
     */
    public static String nearest(String string, List<String> strings, boolean substring) {
        if (strings == null || string == null || strings.isEmpty()) return null;

        int lev = Integer.MAX_VALUE;
        String min = strings.get(0);
        for (String s : strings) {
            int curLev = levenshtein(string, s, substring);
            if (curLev < lev) {
                lev = curLev;
                min = s;
            }
        }

        return min;
    }

    public static String nearest(String string, List<String> strings){
        return nearest(string, strings, false);
    }

    /**
     * @param string
     * @param strings
     * @param substring Whether to match the whole string or to look for the lowest substring match.
     * @return Sorted list of strings based on levenshtein distance
     */
    public static List<String> nearestSorted(String string, List<String> strings, boolean substring) {
        if (strings == null || string == null || strings.isEmpty()) return null;

        final int[] levs = new int[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            levs[i] = levenshtein(string, strings.get(i), substring);
        }

        final List<String> listCopy = new ArrayList<>(strings);
        List<String> sortedList = new ArrayList<String>(listCopy);
        Collections.sort(sortedList, new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                return levs[listCopy.indexOf(left)] - levs[listCopy.indexOf(right)];
            }
        });

        return sortedList;
    }

    public static List<String> nearestSorted(String string, List<String> strings) {
        return nearestSorted(string, strings, false);
    }

}
