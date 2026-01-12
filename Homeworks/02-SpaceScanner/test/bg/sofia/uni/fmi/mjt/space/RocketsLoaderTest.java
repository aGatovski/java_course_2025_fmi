package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RocketsLoaderTest {
    private static final String HEADER_LINE =
        ",Name,Wiki,Rocket Height";
    private static final String VALID_ROCKET_LINE =
        "0,Tsyklon-3, https://en.wikipedia.org/wiki/Tsyklon-3, 39.0 m";

    @Test
    void testReaderIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> RocketsLoader.load(null),
            "Expected load to throw IllegalArgumentException when reader is null");
    }

    @Test
    void testCorrectLoad() {
        Reader reader = new StringReader(HEADER_LINE + System.lineSeparator() + VALID_ROCKET_LINE);
        List<Rocket> rockets = RocketsLoader.load(reader);

        assertEquals(1, rockets.size(), "Expected exactly one mission to be loaded");

        Rocket rocket = rockets.get(0);

        assertEquals("0", rocket.id());
        assertEquals("Tsyklon-3", rocket.name());
        assertTrue(rocket.wiki().isPresent(), "Wiki should be present");
        assertEquals("https://en.wikipedia.org/wiki/Tsyklon-3", rocket.wiki().get());
        assertTrue(rocket.height().isPresent(), "Height should be present");
        assertEquals(39.0, rocket.height().get(), 0.001);
    }
}