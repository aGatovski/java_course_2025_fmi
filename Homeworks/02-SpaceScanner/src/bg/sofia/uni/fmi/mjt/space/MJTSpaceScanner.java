package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        missions = MissionsLoader.load(missionsReader);
        rockets = RocketsLoader.load(rocketsReader);
        this.secretKey = secretKey;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return Collections.unmodifiableList(missions);
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
        return "";
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
        return List.of();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return Map.of();
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        return Map.of();
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return Collections.unmodifiableList(rockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        return List.of();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return Map.of();
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        return List.of();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {

    }
}
