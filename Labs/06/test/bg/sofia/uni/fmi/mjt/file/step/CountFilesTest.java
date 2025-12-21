package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CountFilesTest {
    private CountFiles cut = new CountFiles();

    @Test
    void testProcessThrowsIllegalArgumentExceptionWhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cut.process(null),
            "Input collection of files is null");
    }

    @Test
    void testProcessReturnsCollectionSize() {
        Collection<File> input = new ArrayList<>();
        input.add(new File("File 1"));
        input.add(new File("File 2"));

        assertEquals(2, cut.process(input),"Should return the count of 2!");
    }
}