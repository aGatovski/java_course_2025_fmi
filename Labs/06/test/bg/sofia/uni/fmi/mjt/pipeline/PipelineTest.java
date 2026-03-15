package bg.sofia.uni.fmi.mjt.pipeline;

import bg.sofia.uni.fmi.mjt.pipeline.stage.Stage;
import bg.sofia.uni.fmi.mjt.pipeline.step.Step;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PipelineTest {
    @Mock
    private Stage mockStage = mock();

    @Test
    void testStartThrowsIllegalArgumentExceptionWhenInitialStepIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> Pipeline.start(null),
            "Initial step is null should throw IllegalArgumentException");
    }

    @Test
    void testStartReturnsStageWhenStepIsValid() {
        Pipeline<Object, Object> pipeline = Pipeline.start(mockStage);
        assertNotNull(pipeline, "A new stage starting with the given initial step is returned");
    }
}