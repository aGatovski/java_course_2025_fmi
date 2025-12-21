package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import bg.sofia.uni.fmi.mjt.file.exception.EmptyFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckEmptyFileTest {
    //class under test
    private CheckEmptyFile cut = new CheckEmptyFile();

    @Test
    void testProcessThrowsEmptyFileExceptionWhenFileIsNull() {
        assertThrows(EmptyFileException.class, () -> cut.process(null),
            "Input file is empty should throw EmptyFileException");
    }

    @Test
    void testProcessThrowsEmptyFileExceptionWhenFileContentIsNull() {
        File mockFile = Mockito.mock(File.class);
        when(mockFile.getContent()).thenReturn(null);

        assertThrows(EmptyFileException.class, () -> cut.process(mockFile),
            "Input file is empty should throw EmptyFileException");
    }

    @Test
    void testProcessThrowsEmptyFileExceptionWhenFileContentIsEmpty() {
        File emptyFile = new File("");

        assertThrows(EmptyFileException.class, () -> cut.process(emptyFile),
            "Input file is empty should throw EmptyFileException");
    }

    @Test
    void testProcessReturnsSameFile(){
        File contentFile = new File("File content");

        assertSame(contentFile, cut.process(contentFile),"Method returns the same copy of file!");
    }
}