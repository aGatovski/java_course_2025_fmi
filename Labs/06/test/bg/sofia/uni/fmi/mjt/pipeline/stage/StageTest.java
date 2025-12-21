package bg.sofia.uni.fmi.mjt.pipeline.stage;

import bg.sofia.uni.fmi.mjt.pipeline.step.Step;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageTest {
    @Mock
    private Step mockStep = mock();

    @Mock
    private Step mockStep2 = mock();

    @Test
    void testStartThrowsIllegalArgumentExceptionWhenInitialStepIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> Stage.start(null),
            "Initial step is null should throw IllegalArgumentException");
    }

    @Test
    void testStartReturnsStageWhenStepIsValid() {
        Stage<Object, Object> stage = Stage.start(mockStep);
        assertNotNull(stage, "A new stage starting with the given initial step is returned");
    }

    @Test
    void testAddStepThrowsExceptionWhenStepIsNull() {
        Stage<Object, Object> stage = Stage.start(mockStep);
        assertThrows(IllegalArgumentException.class,
            () -> stage.addStep(null),
            "Initial step is null should throw IllegalArgumentException");
    }

    @Test
    void testAddStepReturns() {
        Stage<Object, Object> stage = Stage.start(mockStep);
        stage.addStep(mockStep2);
        assertNotNull(stage, "A new stage starting with the given initial step is returned");
    }

    @Test
    void testExecute() {
        String firstInput = "input";
        Integer secondInput = 42;
        String thirdInput = "final";

        when(mockStep.process(firstInput)).thenReturn(secondInput);
        when(mockStep2.process(secondInput)).thenReturn(thirdInput);

        Stage<String, String> stage = Stage.start(mockStep);
        stage.addStep(mockStep2);

        String result = stage.execute(firstInput);

        assertEquals(thirdInput, result, "The stage should pass the output of step to step2");

        verify(mockStep).process(firstInput);
        verify(mockStep2).process(secondInput);
    }
}