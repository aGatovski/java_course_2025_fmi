package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SplitFileTest {
    private SplitFile cut = new SplitFile();

    @Test
    void testProcessThrowsIllegalArgumentExceptionWhenFileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cut.process(null),
            "Input file is empty should throw IllegalArgumentException");
    }

    @Test
    void testProcessThrowsIllegalArgumentExceptionWhenFileContentIsNull() {
        File mockFile = Mockito.mock(File.class);
        when(mockFile.getContent()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cut.process(mockFile),
            "Input file is empty should throw IllegalArgumentException");
    }

    @Test
    void testProcessReturnNewSet() {
        File contentFile = new File("Content Content2 Content");

        Set<File> result = new HashSet<>();
        result.add(new File("Content"));
        result.add(new File("Content2"));

        assertEquals(result.size(), cut.process(contentFile).size(), "Size should be equal!");
    }

}