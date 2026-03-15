package bg.sofia.uni.fmi.mjt.space.mission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MissionTest {
    
    @Test
    void testMissionOfThrowsIllegalArgumentExceptionWhenLineNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> Mission.of(null));
        assertThrows(IllegalArgumentException.class, () -> Mission.of(""));
    }
}