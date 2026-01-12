package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MJTSpaceScannerTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private static final String MISSIONS_CSV = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Failure
        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
        25,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Sat May 30, 2020",Falcon 9 Block 5 | SpaceX Demo-2,StatusActive,"50.0 ",Success
        """;

    private static final String ROCKETS_CSV = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
        """;

    private static SecretKey secretKey;
    private MJTSpaceScanner scanner;

    @BeforeAll
    static void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        secretKey = keyGenerator.generateKey();
    }

    @BeforeEach
    void setUp() {
        StringReader missionsReader = new StringReader(MISSIONS_CSV);
        StringReader rocketsReader = new StringReader(ROCKETS_CSV);
        scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
    }

    @Test
    void testGetAllMissions() {
        Collection<Mission> missions = scanner.getAllMissions();
        assertEquals(7, missions.size(), "Get 6 missions");
    }

    @Test
    void testGetAllRockets() {
        Collection<Rocket> rockets = scanner.getAllRockets();
        assertEquals(3, rockets.size(), "Get 3 Rockets");
    }

    @Test
    void testGetAllMissionsByStatusThrowsIllegalArgumentExceptionWhenStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getAllMissions(null), "Status cannot be null!");
    }

    @Test
    void testGetAllMissionsByStatus() {
        Collection<Mission> successMissions = scanner.getAllMissions(MissionStatus.SUCCESS);
        assertEquals(4, successMissions.size());

        Collection<Mission> failedMissions = scanner.getAllMissions(MissionStatus.FAILURE);
        assertEquals(3, failedMissions.size());
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsIllegalArgumentExceptionWhenDateNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(null, null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsTimeFrameMismatchExceptionWhenDateToBeforeDateFrom() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);
        assertThrows(TimeFrameMismatchException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(to, from));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);

        String company = scanner.getCompanyWithMostSuccessfulMissions(from, to);
        assertTrue(List.of("SpaceX", "CASC", "Roscosmos").contains(company));
    }

    @Test
    void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> map = scanner.getMissionsPerCountry();

        assertTrue(map.containsKey("USA"));
        assertTrue(map.containsKey("China"));
        assertTrue(map.containsKey("Kazakhstan"));

        assertEquals(2, map.get("China").size());
        assertEquals(4, map.get("USA").size());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsThrowsIllegalArgumentExceptionWhenMissionStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE));
    }


    @Test
    void testGetTopNLeastExpensiveMissionsThrowsIllegalArgumentExceptionWhenRocketStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsThrowsIllegalArgumentExceptionWhenNIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        List<Mission> result =
            scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(2, result.size());
        assertEquals("CASC", result.get(0).company());
        assertEquals("SpaceX", result.get(1).company());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany(){
        Map<String, String> map = scanner.getMostDesiredLocationForMissionsPerCompany();

        assertTrue(map.containsKey("CASC"), "Result map should include CASC.");
        assertTrue(map.containsKey("ULA"), "Result map should include ULA.");

        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", map.get("SpaceX"));
        assertEquals("Site 200/39, Baikonur Cosmodrome, Kazakhstan", map.get("Roscosmos"));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsIllegalArgumentExceptionWhen(){
        assertThrows(IllegalArgumentException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(null, null));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsTimeFrameMismatchExceptionWhenDateToBeforeDateFrom() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);
        assertThrows(TimeFrameMismatchException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(to, from));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);

        Map<String, String> result = scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to);

        assertTrue(result.containsKey("SpaceX"));
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", result.get("SpaceX"));
    }

    @Test
    void testGetTopNTallestRocketsThrowsIllegalArgumentExceptionWhenNIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getTopNTallestRockets(-1));
    }

    @Test
    void testGetTopNTallestRockets() {
        List<Rocket> result = scanner.getTopNTallestRockets(2);

        assertEquals(2, result.size());
        assertEquals("Tsyklon-3", result.get(0).name());
        assertEquals(39.0, result.get(0).height().get());
        assertEquals("Tsyklon-4M", result.get(1).name());
    }

    @Test
    void testGetWikiPageForRocket() {
        Map<String, Optional<String>> wikis = scanner.getWikiPageForRocket();

        assertEquals(3, wikis.size());
        assertTrue(wikis.containsKey("Tsyklon-3"));
        assertTrue(wikis.get("Tsyklon-3").isPresent());
        assertEquals("https://en.wikipedia.org/wiki/Tsyklon-3", wikis.get("Tsyklon-3").get());
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsWhenMissionStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsWhenRocketStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsWhenNIsInvalid() {
        assertThrows(IllegalArgumentException.class,
            () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));

        assertThrows(IllegalArgumentException.class,
            () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        List<String> result = scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgumentExceptionWhenOutputStreamIsNull() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);

        assertThrows(IllegalArgumentException.class,
            () -> scanner.saveMostReliableRocket(null, from, to));
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgumentExceptionWhenFromDateIsNull() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate to = LocalDate.of(2020, 12, 31);

        assertThrows(IllegalArgumentException.class,
            () -> scanner.saveMostReliableRocket(outputStream, null, to));
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgumentExceptionWhenToDateIsNull() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate from = LocalDate.of(2020, 1, 1);

        assertThrows(IllegalArgumentException.class,
            () -> scanner.saveMostReliableRocket(outputStream, from, null));
    }

    @Test
    void testSaveMostReliableRocketThrowsTimeFrameMismatchExceptionWhenToIsBeforeFrom() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate from = LocalDate.of(2020, 12, 31);
        LocalDate to = LocalDate.of(2020, 1, 1);

        assertThrows(TimeFrameMismatchException.class,
            () -> scanner.saveMostReliableRocket(outputStream, from, to));
    }

}
