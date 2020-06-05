package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.Calculate;

import static org.junit.Assert.assertEquals;

public class CalculateTests {

    @Before
    public void setUp() {
        System.out.println("Starting unit tests for PlaybackQueue.");
    }

    @Test
    public void testLevenshtein() {
        String[][] strings = new String[][]{
                {"", ""},
                {"yo", ""},
                {"", "yo"},
                {"yo", "yo"},
                {"tier", "tor"},
                {"saturday", "sunday"},
                {"mist", "dist"},
                {"tier", "tor"},
                {"kitten", "sitting"},
                {"stop", "tops"},
                {"rosettacode", "raisethysword"},
                {"mississippi", "swiss miss"}
        };
        int[] expected = new int[]{
                0, 2, 2, 0, 2, 3, 1, 2, 3, 2, 8, 8
        };

        for (int i = 0; i < strings.length; i++) {
            assertEquals(expected[i], Calculate.levenshtein(strings[i][0], strings[i][1]));
        }
    }

    @Test
    public void testLevenshteins() {
        String query = "query";
        String[] strings = new String[]{
                "query",
                "not query",
                "banana",
                "apple",
                "queen"
        };
        int[] expected = new int[]{
                0, 4, 6, 5, 2
        };

        int[] levs = Calculate.levenshteins(query, Arrays.asList(strings));
        for (int i = 0; i < strings.length; i++) {
            assertEquals(expected[i], levs[i]);
        }
    }

    @Test
    public void testNearest() {
        String string = "fro";
        String[] strings = new String[]{"low", "pro", "go", "so", "slow", "orp", "joe"};
        String expected = "pro";

        assertEquals(expected, Calculate.nearest(string, Arrays.asList(strings)));

        strings = new String[]{"low", "pro", "go", "so", "slow", "orp", "joe", "fro"};
        expected = "fro";

        assertEquals(expected, Calculate.nearest(string, Arrays.asList(strings)));
    }

    @Test
    public void testNearestSorted() {
        String string = "fro";
        String[] strings = new String[]{"", "ro", "bingolino", "fro", "slows", "vra"};
        String[] expected = new String[]{"fro", "ro", "vra", "", "slows", "bingolino"};

        List<String> sorted = Calculate.nearestSorted(string, Arrays.asList(strings));
        for (int i = 0; i < strings.length; i++) {
            assertEquals(expected[i], sorted.get(i));
        }
    }

    @After
    public void tearDown() {
        System.out.println("Finished unit tests for Calculate.");
    }

}
