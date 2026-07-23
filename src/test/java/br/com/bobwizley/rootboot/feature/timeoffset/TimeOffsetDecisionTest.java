package br.com.bobwizley.rootboot.feature.timeoffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimeOffsetDecisionTest {

    @Test
    void freshWorldEnabledAddsOneDayAndMarks() {
        TimeOffsetDecision decision = TimeOffsetDecision.evaluate(false, true, true);

        assertEquals(TimeOffsetDecision.ONE_DAY_TICKS, decision.ticksToAdd());
        assertTrue(decision.shouldMarkEvaluated());
    }

    @Test
    void freshWorldDisabledAddsNothingButStillMarks() {
        TimeOffsetDecision decision = TimeOffsetDecision.evaluate(false, false, true);

        assertEquals(0L, decision.ticksToAdd());
        assertTrue(decision.shouldMarkEvaluated());
    }

    @Test
    void preExistingWorldEnabledIsNotShiftedButStillMarks() {
        TimeOffsetDecision decision = TimeOffsetDecision.evaluate(false, true, false);

        assertEquals(0L, decision.ticksToAdd());
        assertTrue(decision.shouldMarkEvaluated());
    }

    @Test
    void preExistingWorldDisabledAddsNothingButStillMarks() {
        TimeOffsetDecision decision = TimeOffsetDecision.evaluate(false, false, false);

        assertEquals(0L, decision.ticksToAdd());
        assertTrue(decision.shouldMarkEvaluated());
    }

    @Test
    void alreadyEvaluatedIsNoOp() {
        assertEquals(0L, TimeOffsetDecision.evaluate(true, true, true).ticksToAdd());
        assertFalse(TimeOffsetDecision.evaluate(true, true, true).shouldMarkEvaluated());
        assertEquals(0L, TimeOffsetDecision.evaluate(true, false, false).ticksToAdd());
        assertFalse(TimeOffsetDecision.evaluate(true, false, false).shouldMarkEvaluated());
    }
}
