package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowAttributeSumEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.PublicVoteEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.HumorousErgenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.RomanticErgenka;

import java.util.Arrays;

public class Main {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static int testsPassed = 0;
    private static int testsTotal = 0;

    // Хелпър метод за извеждане на резултатите
    private static void assertTest(boolean condition, String testName) {
        testsTotal++;
        if (condition) {
            System.out.println(GREEN + "✅ PASS: " + testName + RESET);
            testsPassed++;
        } else {
            System.err.println(RED + "❌ FAIL: " + testName + RESET);
        }
    }

    // Хелпър метод за принтиране на масив от участнички
    private static String getErgenkaNames(Ergenka[] ergenkas) {
        return Arrays.stream(ergenkas)
                .map(Ergenka::getName)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("[]");
    }

    public static void main(String[] args) {
        System.out.println("--- Starting ShowAPI Simulation Tests ---");

        // 1. Setup - Initial Ergenkas
        RomanticErgenka anna = new RomanticErgenka("Anna", (short) 25, 10, 3, 50, "Beach");
        HumorousErgenka bella = new HumorousErgenka("Bella", (short) 28, 5, 12, 60);
        RomanticErgenka clara = new RomanticErgenka("Clara", (short) 22, 1, 9, 30, "Mountain");
        HumorousErgenka diana = new HumorousErgenka("Diana", (short) 30, 15, 6, 70);

        Ergenka[] initialErgenkas = {anna, bella, clara, diana};

        // --- Test 1: Rating Change Logic (RomanticErgenka) ---
        testRomanticErgenkaRating(anna);

        // --- Test 2: Rating Change Logic (HumorousErgenka) ---
        testHumorousErgenkaRating(bella);

        // --- Test 3: ShowAPI.organizeDate() ---
        testOrganizeDate(anna, new DateEvent("Park", 5, 60)); // Check if Anna's rating changes

        // --- Test 4: ShowAPI.playRound() ---
        testPlayRound(initialErgenkas);

        // --- Test 5: EliminationRule - LowestRatingEliminationRule ---
        testLowestRatingEliminationRule();

        // --- Test 6: EliminationRule - LowAttributeSumEliminationRule ---
        testLowAttributeSumEliminationRule();

        // --- Test 7: EliminationRule - PublicVoteEliminationRule (Majority) ---
        testPublicVoteEliminationRuleMajority();

        // --- Test 8: EliminationRule - PublicVoteEliminationRule (No Majority) ---
        testPublicVoteEliminationRuleNoMajority();

        // --- Test 9: ShowAPI.eliminateErgenkas() with default rule ---
        testEliminateErgenkasDefaultRule(initialErgenkas);

        System.out.println("\n--- Test Summary ---");
        System.out.println("Total Tests: " + testsTotal);
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println(testsPassed == testsTotal ? GREEN + "All Tests Passed!" + RESET : RED + "Some Tests Failed!" + RESET);
    }

    // =========================================================================
    // 1. RomanticErgenka Rating Test
    // =========================================================================
    private static void testRomanticErgenkaRating(RomanticErgenka ergenka) {
        System.out.println("\n# Test 1: RomanticErgenka Rating (Anna)");

        // Initial rating: 50
        // romance_level=10, humor_level=3. Favorite location: Beach

        // Date 1: Normal date. Location: Beach (Bonus +5). Duration: 60 (No +/-). Tension: 5
        // Change: (10 * 7) / 5 + floor(3 / 3) + 5 = 14 + 1 + 5 = 20
        DateEvent date1 = new DateEvent("Beach", 5, 60);
        int expectedRating1 = ergenka.getRating() + 20; // 50 + 20 = 70
        ergenka.reactToDate(date1);

        assertTest(ergenka.getRating() == expectedRating1,
                "Romantic Rating Test 1: Favorite Location Bonus (+5)");

        // Date 2: Short date. Location: Pool (No +5). Duration: 15 (<30 -> -3). Tension: 7
        // Current rating: 70
        // Change: (10 * 7) / 7 + floor(3 / 3) + (-3) = 10 + 1 - 3 = 8
        DateEvent date2 = new DateEvent("Pool", 7, 15);
        int expectedRating2 = ergenka.getRating() + 8; // 70 + 8 = 78
        ergenka.reactToDate(date2);

        assertTest(ergenka.getRating() == expectedRating2,
                "Romantic Rating Test 2: Short Duration Penalty (-3)");

        // Date 3: Long date. Location: beach (case-insensitive check!). Duration: 150 (>120 -> -2). Tension: 10
        // Current rating: 78
        // Change: (10 * 7) / 10 + floor(3 / 3) + (5 - 2) = 7 + 1 + 3 = 11
        DateEvent date3 = new DateEvent("bEaCh", 10, 150);
        int expectedRating3 = ergenka.getRating() + 11; // 78 + 11 = 89
        ergenka.reactToDate(date3);

        assertTest(ergenka.getRating() == expectedRating3,
                "Romantic Rating Test 3: Long Duration Penalty (-2) and Case-Insensitive Location");
    }

    // =========================================================================
    // 2. HumorousErgenka Rating Test
    // =========================================================================
    private static void testHumorousErgenkaRating(HumorousErgenka ergenka) {
        System.out.println("\n# Test 2: HumorousErgenka Rating (Bella)");

        // Initial rating: 60
        // romance_level=5, humor_level=12.

        // Date 1: Reasonable date. Duration: 60 (>=30 and <=90 -> +4). Tension: 4
        // Change: (12 * 5) / 4 + floor(5 / 3) + 4 = 15 + 1 + 4 = 20
        DateEvent date1 = new DateEvent("Park", 4, 60);
        int expectedRating1 = ergenka.getRating() + 20; // 60 + 20 = 80
        ergenka.reactToDate(date1);

        assertTest(ergenka.getRating() == expectedRating1,
                "Humorous Rating Test 1: Reasonable Duration Bonus (+4)");

        // Date 2: Short date. Duration: 20 (<30 -> -2). Tension: 8
        // Current rating: 80
        // Change: (12 * 5) / 8 + floor(5 / 3) + (-2) = 7 (60/8 = 7.5 -> 7) + 1 - 2 = 6
        DateEvent date2 = new DateEvent("Cafe", 8, 20);
        int expectedRating2 = ergenka.getRating() + 6; // 80 + 6 = 86
        ergenka.reactToDate(date2);

        assertTest(ergenka.getRating() == expectedRating2,
                "Humorous Rating Test 2: Short Duration Penalty (-2)");

        // Date 3: Long date. Duration: 100 (>90 -> -3). Tension: 5
        // Current rating: 86
        // Change: (12 * 5) / 5 + floor(5 / 3) + (-3) = 12 + 1 - 3 = 10
        DateEvent date3 = new DateEvent("Cinema", 5, 100);
        int expectedRating3 = ergenka.getRating() + 10; // 86 + 10 = 96
        ergenka.reactToDate(date3);

        assertTest(ergenka.getRating() == expectedRating3,
                "Humorous Rating Test 3: Long Duration Penalty (-3)");
    }

    // =========================================================================
    // 3. ShowAPI.organizeDate() Test
    // =========================================================================
    private static void testOrganizeDate(RomanticErgenka ergenka, DateEvent dateEvent) {
        System.out.println("\n# Test 3: ShowAPI.organizeDate()");

        // Setup a fresh ShowAPIImpl for the test
        Ergenka[] initial = {ergenka};
        ShowAPI api = new ShowAPIImpl(initial, new EliminationRule[]{});
        int initialRating = ergenka.getRating(); // Rating after Test 1, before Date 4

        // A date event where the expected change is known (e.g., using Date 1 logic from Test 1)
        // Date: Park, Tension 5, Duration 60. Anna's favorite is Beach.
        // Change: (10 * 7) / 5 + floor(3 / 3) + 0 = 14 + 1 + 0 = 15
        int expectedChange = 15;

        api.organizeDate(ergenka, dateEvent);

        // Anna's current rating after Test 1 is 89. Expected: 89 + 15 = 104
        int expectedRatingAfterDate = initialRating + expectedChange;

        assertTest(ergenka.getRating() == expectedRatingAfterDate,
                "Organize Date Test: Target Ergenka rating updated correctly.");
    }

    // =========================================================================
    // 4. ShowAPI.playRound() Test
    // =========================================================================
    private static void testPlayRound(Ergenka[] initialErgenkas) {
        System.out.println("\n# Test 4: ShowAPI.playRound()");

        // Create a new set of ergenkas to avoid contamination from previous tests
        RomanticErgenka a = new RomanticErgenka("A_Round", (short) 25, 10, 3, 50, "Park");
        HumorousErgenka b = new HumorousErgenka("B_Round", (short) 28, 5, 12, 60);
        Ergenka[] ergenkas = {a, b};

        ShowAPI api = new ShowAPIImpl(ergenkas, new EliminationRule[]{});

        // Date Event: Park (A's favorite), Tension 5, Duration 60 (B's reasonable)
        DateEvent dateEvent = new DateEvent("Park", 5, 60);

        // A_Round (Romantic): Change = (10*7)/5 + floor(3/3) + 5 = 14 + 1 + 5 = 20. New Rating: 50+20 = 70
        // B_Round (Humorous): Change = (12*5)/5 + floor(5/3) + 4 = 12 + 1 + 4 = 17. New Rating: 60+17 = 77
        int expectedARating = 70;
        int expectedBRating = 77;

        api.playRound(dateEvent);

        // Check if both ratings have been updated
        assertTest(a.getRating() == expectedARating && b.getRating() == expectedBRating,
                "Play Round Test: All ergenkas' ratings updated correctly.");

        // Check getErgenkas()
        assertTest(api.getErgenkas().length == 2 && api.getErgenkas()[0].getName().equals("A_Round"),
                "Get Ergenkas Test: Returns the current list of participants.");
    }

    // =========================================================================
    // 5. LowestRatingEliminationRule Test
    // =========================================================================
    private static void testLowestRatingEliminationRule() {
        System.out.println("\n# Test 5: LowestRatingEliminationRule");

        // Setup: Low (20), Medium (40), Low (20), High (60)
        Ergenka e1 = new RomanticErgenka("Low_1", (short) 20, 5, 5, 20, "A");
        Ergenka e2 = new HumorousErgenka("Med_1", (short) 20, 5, 5, 40);
        Ergenka e3 = new RomanticErgenka("Low_2", (short) 20, 5, 5, 20, "B");
        Ergenka e4 = new HumorousErgenka("High_1", (short) 20, 5, 5, 60);

        Ergenka[] participants = {e1, e2, e3, e4};
        EliminationRule rule = new LowestRatingEliminationRule();

        // Expected: e1 and e3 are eliminated (rating 20 is the lowest)
        Ergenka[] remaining = rule.eliminateErgenkas(participants);
        String remainingNames = getErgenkaNames(remaining);

        assertTest(remaining.length == 2 && remainingNames.contains("Med_1") && remainingNames.contains("High_1"),
                "Lowest Rating Rule Test: Eliminates all with the lowest rating (20).");
    }

    // =========================================================================
    // 6. LowAttributeSumEliminationRule Test
    // =========================================================================
    private static void testLowAttributeSumEliminationRule() {
        System.out.println("\n# Test 6: LowAttributeSumEliminationRule");

        // Setup: Sums: 10, 15, 5, 20
        Ergenka e1 = new RomanticErgenka("Sum_10", (short) 20, 5, 5, 20, "A"); // Sum = 10
        Ergenka e2 = new HumorousErgenka("Sum_15", (short) 20, 7, 8, 40); // Sum = 15
        Ergenka e3 = new RomanticErgenka("Sum_5", (short) 20, 2, 3, 20, "B"); // Sum = 5
        Ergenka e4 = new HumorousErgenka("Sum_20", (short) 20, 10, 10, 60); // Sum = 20

        Ergenka[] participants = {e1, e2, e3, e4};
        int threshold = 12; // Rules: Sum < 12 are eliminated
        EliminationRule rule = new LowAttributeSumEliminationRule(threshold);

        // Expected: e1 (10) and e3 (5) are eliminated
        Ergenka[] remaining = rule.eliminateErgenkas(participants);
        String remainingNames = getErgenkaNames(remaining);

        assertTest(remaining.length == 2 && remainingNames.contains("Sum_15") && remainingNames.contains("Sum_20"),
                "Low Attribute Sum Rule Test: Eliminates participants with (romance + humor) < 12.");
    }

    // =========================================================================
    // 7. PublicVoteEliminationRule (Majority) Test
    // =========================================================================
    private static void testPublicVoteEliminationRuleMajority() {
        System.out.println("\n# Test 7: PublicVoteEliminationRule (Majority)");

        // Setup: 4 participants, 7 votes. Majority needed: floor(7/2) + 1 = 4
        Ergenka eA = new RomanticErgenka("V_A", (short) 20, 5, 5, 20, "A");
        Ergenka eB = new HumorousErgenka("V_B", (short) 20, 5, 5, 40);
        Ergenka eC = new RomanticErgenka("V_C", (short) 20, 5, 5, 20, "B");
        Ergenka eD = new HumorousErgenka("V_D", (short) 20, 5, 5, 60);

        Ergenka[] participants = {eA, eB, eC, eD};
        // V_A has 4 votes (majority)
        String[] votes = {"V_A", "V_B", "V_A", "V_C", "V_A", "V_A", "V_C"};
        EliminationRule rule = new PublicVoteEliminationRule(votes);

        // Expected: V_A is eliminated
        Ergenka[] remaining = rule.eliminateErgenkas(participants);
        String remainingNames = getErgenkaNames(remaining);

        assertTest(remaining.length == 3 && !remainingNames.contains("V_A"),
                "Public Vote Rule Test 1: Eliminates V_A with 4/7 votes (Majority).");
    }

    // =========================================================================
    // 8. PublicVoteEliminationRule (No Majority) Test
    // =========================================================================
    private static void testPublicVoteEliminationRuleNoMajority() {
        System.out.println("\n# Test 8: PublicVoteEliminationRule (No Majority)");

        // Setup: 4 participants, 7 votes. Majority needed: floor(7/2) + 1 = 4
        Ergenka eA = new RomanticErgenka("V_A", (short) 20, 5, 5, 20, "A");
        Ergenka eB = new HumorousErgenka("V_B", (short) 20, 5, 5, 40);
        Ergenka eC = new RomanticErgenka("V_C", (short) 20, 5, 5, 20, "B");
        Ergenka eD = new HumorousErgenka("V_D", (short) 20, 5, 5, 60);

        Ergenka[] participants = {eA, eB, eC, eD};
        // No one has majority (V_A: 3, V_B: 2, V_C: 2)
        String[] votes = {"V_A", "V_B", "V_A", "V_C", "V_B", "V_C", "V_A"};
        EliminationRule rule = new PublicVoteEliminationRule(votes);

        // Expected: No one is eliminated
        Ergenka[] remaining = rule.eliminateErgenkas(participants);

        assertTest(remaining.length == 4,
                "Public Vote Rule Test 2: No one is eliminated when no majority is reached.");
    }

    // =========================================================================
    // 9. ShowAPI.eliminateErgenkas() with default rule Test
    // =========================================================================
    private static void testEliminateErgenkasDefaultRule(Ergenka[] initialErgenkas) {
        System.out.println("\n# Test 9: ShowAPI.eliminateErgenkas() with default rule");

        // Create a new set of ergenkas for a clean test
        RomanticErgenka a = new RomanticErgenka("Default_A", (short) 25, 10, 3, 100, "Park");
        HumorousErgenka b = new HumorousErgenka("Default_B", (short) 28, 5, 12, 50); // Lowest Rating
        RomanticErgenka c = new RomanticErgenka("Default_C", (short) 22, 1, 9, 100, "Mountain");

        Ergenka[] ergenkas = {a, b, c};

        // Initializing with an empty array for default rules,
        // but the main logic inside the test uses a fresh array to test the EliminationRule itself.
        // For the API test, we need a ShowAPIImpl object that uses the internal list
        ShowAPIImpl api = new ShowAPIImpl(ergenkas, new EliminationRule[]{}); // Default rules are LowestRating

        // This will apply the default LowestRatingEliminationRule because the parameter is an empty array
        api.eliminateErgenkas(new EliminationRule[]{});

        Ergenka[] remaining = api.getErgenkas();
        String remainingNames = getErgenkaNames(remaining);

        // Expected: 'Default_B' (rating 50) is eliminated, 'Default_A' and 'Default_C' (rating 100) remain
        assertTest(remaining.length == 2 && remainingNames.contains("Default_A") && remainingNames.contains("Default_C"),
                "ShowAPI Default Elimination Test: Applies LowestRatingRule when no rules are provided.");
    }

}