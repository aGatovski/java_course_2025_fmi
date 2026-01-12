package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private final Rijndael cipher;
    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        missions = MissionsLoader.load(missionsReader);
        rockets = RocketsLoader.load(rocketsReader);
        this.secretKey = secretKey;
        this.cipher = new Rijndael(secretKey);
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        return missions.stream().filter(mission -> mission.missionStatus() == missionStatus).toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date from or to cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Date to cannot come before date from!");
        }

        Map<String, Long> resultMap = missions.stream()// FIX: Use !isBefore and !isAfter to make it INCLUSIVE
            .filter(mission -> !mission.date().isBefore(from) && !mission.date().isAfter(to))
            .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));

        return resultMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream().collect(Collectors.groupingBy(mission -> {
            String location = mission.location();
            int lastCommaIndex = location.lastIndexOf(',');

            if (lastCommaIndex == -1) {
                return location.trim();
            }
            // Take everything after the last comma and remove spaces
            return location.substring(lastCommaIndex + 1).trim();
        }, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null!");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be negative!");
        }

        return missions.stream().filter(mission -> mission.missionStatus() == missionStatus)
            .filter(mission -> mission.rocketStatus() == rocketStatus).filter(mission -> mission.cost().isPresent())
            .sorted(Comparator.comparingDouble(mission -> mission.cost().get())).limit(n).toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        Map<String, Map<String, Long>> mapCompanyLocationsCount = missions.stream().collect(
            Collectors.groupingBy(Mission::company, Collectors.groupingBy(Mission::location, Collectors.counting())));

        return mapCompanyLocationsCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            return entry.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                .orElse("");
        }));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date from or to cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Date to cannot come before date from!");
        }
        //Company, LocationSuccess,Long
        Map<String, Map<String, Long>> mapCompanyLocationsSuccessfulMissionsCount =
            missions.stream().filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .filter(mission -> !mission.date().isBefore(from) && !mission.date().isAfter(to)).collect(
                    Collectors.groupingBy(Mission::company,
                        Collectors.groupingBy(Mission::location, Collectors.counting())));

        return mapCompanyLocationsSuccessfulMissionsCount.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                return entry.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                    .orElse("");
            }));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be negative!");
        }

        return rockets.stream().filter(rocket -> rocket.height().isPresent())
            .sorted(Comparator.comparingDouble((Rocket rocket) -> rocket.height().get()).reversed()).limit(n).toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream().collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null!");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be negative!");
        }

        Map<String, String> mapRocketNameWiki = rockets.stream().filter(rocket -> rocket.wiki().isPresent())
            .collect(Collectors.toMap(Rocket::name, rocket -> rocket.wiki().get()));

        return missions.stream().filter(mission -> mission.missionStatus() == missionStatus)
            .filter(mission -> mission.rocketStatus() == rocketStatus).filter(mission -> mission.cost().isPresent())
            .sorted(Comparator.comparingDouble((Mission mission) -> mission.cost().get()).reversed()).limit(n)
            .map(mission -> mission.detail().rocketName()).map(mapRocketNameWiki::get).toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null!");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date from or to cannot be null!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Date to cannot come before date from!");
        }

        Map<String, List<Mission>> mapRocketMissions =
            missions.stream().filter(mission -> !mission.date().isBefore(from) && !mission.date().isAfter(to))
                .collect(Collectors.groupingBy(mission -> mission.detail().rocketName()));

        String mostReliableRocket = mapRocketMissions.entrySet().stream()
            .max(Comparator.comparingDouble(entry -> calculateReliability(entry.getValue()))).map(Map.Entry::getKey)
            .orElse("");

        try (var inputStream = new ByteArrayInputStream(mostReliableRocket.getBytes())) {
            cipher.encrypt(inputStream, outputStream);
        } catch (Exception e) {
            throw new CipherException("Encryption failed", e);
        }
    }

    private double calculateReliability(List<Mission> missions) {
        if (missions.isEmpty()) {
            return 0.0;
        }

        int totalMissions = missions.size();
        long successfulMissions = missions.stream()
            .filter(m -> m.missionStatus() == MissionStatus.SUCCESS)
            .count();

        long unsuccessfulMissions = totalMissions - successfulMissions;

        return (2.0 * successfulMissions + unsuccessfulMissions) / (2.0 * totalMissions);
    }
}
