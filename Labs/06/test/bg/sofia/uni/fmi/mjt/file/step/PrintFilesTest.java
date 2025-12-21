package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PrintFilesTest {
    private PrintFiles cut = new PrintFiles();

    @Test
    void testProcessThrowsIllegalArgumentExceptionWhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cut.process(null),
            "Input collection of files is null");
    }

    @Test
    void testProcessPrintsCollection() {
        Collection<File> input = new ArrayList<>();
        input.add(new File("File 1"));
        input.add(new File("File 2"));

        assertSame(input, cut.process(input), "Method returns the same copy of file!");
    }
}