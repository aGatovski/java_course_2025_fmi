package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpperCaseFileTest {
    private UpperCaseFile cut = new UpperCaseFile();

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
    void testProcessFileToUpperCase() {
        File lowerCaseFile = new File("lower case");
        File upperCaseFile = new File("LOWER CASE");


        assertEquals(upperCaseFile.getContent(), cut.process(lowerCaseFile).getContent(),
            "Files should be equal after proccess!");
    }
}