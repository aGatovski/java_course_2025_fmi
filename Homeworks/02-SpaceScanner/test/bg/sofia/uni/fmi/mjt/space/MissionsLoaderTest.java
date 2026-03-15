package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class MissionsLoaderTest {
    private static final String HEADER_LINE =
        "Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission";
    private static final String VALID_MISSION_LINE =
        "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";

    @Test
    void testReaderIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> MissionsLoader.load(null),
            "Expected load to throw IllegalArgumentException when reader is null");
    }

    @Test
    void testCorrectLoad() {
        Reader reader = new StringReader(HEADER_LINE + System.lineSeparator() + VALID_MISSION_LINE);
        List<Mission> missions = MissionsLoader.load(reader);

        assertEquals(1, missions.size(), "Expected exactly one mission to be loaded");

        Mission mission = missions.get(0);

        assertEquals("0", mission.id());
        assertEquals("SpaceX", mission.company());
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", mission.location(), "Location should be parsed and cleaned");
        assertEquals(LocalDate.of(2020, 8, 7), mission.date(), "Date should be parsed correctly");
        assertEquals("Falcon 9 Block 5", mission.detail().rocketName(), "Rocket name should be extracted from Detail");
        assertEquals("Starlink V1 L9 & BlackSky", mission.detail().payload(), "Payload should be extracted from Detail");
        assertEquals(RocketStatus.STATUS_ACTIVE, mission.rocketStatus(), "Rocket status should be parsed to Enum");
        assertEquals(MissionStatus.SUCCESS, mission.missionStatus(), "Mission status should be parsed to Enum");
        assertTrue(mission.cost().isPresent(), "Cost should be present");
        assertEquals(50.0, mission.cost().get(), 0.001, "Cost value should match");
    }

}
