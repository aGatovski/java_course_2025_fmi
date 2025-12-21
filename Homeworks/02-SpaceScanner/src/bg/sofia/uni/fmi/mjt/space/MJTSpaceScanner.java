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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MJTSpaceScanner implements SpaceScannerAPI{
    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {

    }

    @Override
    public Collection<Mission> getAllMissions() {
        return List.of();
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        return List.of();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        return "";
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return Map.of();
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
        return List.of();
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
