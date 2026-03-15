package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("--- 1. Generating Security Key ---");
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            System.out.println("Key generated successfully.");

            System.out.println("\n--- 2. Loading Data Files ---");
            // Ensure these files are in your project root directory
            try (Reader missionsReader = new FileReader("all-missions-from-1957.csv");
                 Reader rocketsReader = new FileReader("all-rockets-from-1957.csv")) {

                MJTSpaceScanner scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

                // --- TEST 1: Data Loading ---
                Collection<Mission> allMissions = scanner.getAllMissions();
                Collection<Rocket> allRockets = scanner.getAllRockets();
                System.out.println("Total Missions Loaded: " + allMissions.size());
                System.out.println("Total Rockets Loaded: " + allRockets.size());

                if (allMissions.isEmpty() || allRockets.isEmpty()) {
                    System.err.println("CRITICAL: Data failed to load. Check CSV paths and parsing logic.");
                    return;
                }

                // --- TEST 2: Filtering ---
                System.out.println("\n--- 3. Testing Filters ---");
                Collection<Mission> successes = scanner.getAllMissions(MissionStatus.SUCCESS);
                System.out.println("Number of Successful Missions: " + successes.size());

                List<Mission> topCheapMissions = scanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
                System.out.println("Top 3 Cheapest Successful Active Missions:");
                topCheapMissions.forEach(m -> System.out.printf(" - %s ($%.2fM) by %s%n",
                    m.detail().rocketName(), m.cost().orElse(0.0), m.company()));

                // --- TEST 3: Complex Aggregation ---
                System.out.println("\n--- 4. Testing Aggregation ---");
                Map<String, Collection<Mission>> missionsByCountry = scanner.getMissionsPerCountry();
                System.out.println("Countries found: " + missionsByCountry.keySet().size());
                if (missionsByCountry.containsKey("USA")) {
                    System.out.println("Missions in USA: " + missionsByCountry.get("USA").size());
                }

                Map<String, String> desiredLocations = scanner.getMostDesiredLocationForMissionsPerCompany();
                System.out.println("Most desired location for SpaceX: " + desiredLocations.get("SpaceX"));

                // --- TEST 4: Encryption & Reliability ---
                System.out.println("\n--- 5. Testing Encryption (Most Reliable Rocket) ---");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Calculate for a specific timeframe (e.g., last 10 years)
                LocalDate from = LocalDate.of(2010, 1, 1);
                LocalDate to = LocalDate.of(2023, 1, 1);

                System.out.println("Calculating most reliable rocket between 2010 and 2023...");
                scanner.saveMostReliableRocket(outputStream, from, to);

                byte[] encryptedData = outputStream.toByteArray();
                System.out.println("Encrypted Data Size: " + encryptedData.length + " bytes");

                // Decrypt to verify
                System.out.println("Decrypting result...");
                Rijndael decryptor = new Rijndael(secretKey);
                ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();
                decryptor.decrypt(new ByteArrayInputStream(encryptedData), decryptedOutput);

                String reliableRocket = decryptedOutput.toString();
                System.out.println("RESULT: The most reliable rocket is: " + reliableRocket);

            } catch (Exception e) {
                System.err.println("Error during processing:");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Setup error:");
            e.printStackTrace();
        }
    }
}