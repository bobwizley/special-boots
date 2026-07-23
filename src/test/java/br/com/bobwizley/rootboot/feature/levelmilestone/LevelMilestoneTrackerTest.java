package br.com.bobwizley.rootboot.feature.levelmilestone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LevelMilestoneTrackerTest {

    @Test
    void crossingASingleMultipleOfFiveTriggers() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(3, 5);

        assertTrue(result.triggered());
        assertEquals(5, result.newBaseline());
    }

    @Test
    void crossingTheIntervalTriggersEvenWhenFinalLevelIsNotAMultipleOfFive() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(4, 6);

        assertTrue(result.triggered());
        assertEquals(6, result.newBaseline());
    }

    @Test
    void jumpingAcrossSeveralMilestonesStillTriggersOnce() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(3, 17);

        assertTrue(result.triggered());
        assertEquals(17, result.newBaseline());
    }

    @Test
    void risingWithoutCrossingAMultipleOfFiveDoesNotTrigger() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(5, 6);

        assertFalse(result.triggered());
        assertEquals(6, result.newBaseline());
    }

    @Test
    void levelDecreaseNeverTriggersAndRebasesSilently() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(10, 4);

        assertFalse(result.triggered());
        assertEquals(4, result.newBaseline());
    }

    @Test
    void unchangedLevelDoesNotTrigger() {
        LevelMilestoneTracker.Result result = LevelMilestoneTracker.evaluate(6, 6);

        assertFalse(result.triggered());
        assertEquals(6, result.newBaseline());
    }
}
