package br.com.bobwizley.rootboot.feature.levelmilestone;

/**
 * Pure decision logic for Level Milestone, isolated from Minecraft so it can be unit-tested.
 * The trigger is the ascending crossing of at least one multiple of 5, not the final level
 * being a multiple of 5 itself: 4 -&gt; 6 is eligible even though 6 is not a multiple of 5. A
 * jump across several milestones still triggers exactly once. Level decreases never trigger
 * and always rebase silently to the new, lower level.
 */
public final class LevelMilestoneTracker {

    private static final int MILESTONE_INTERVAL = 5;

    private LevelMilestoneTracker() {
    }

    public static Result evaluate(int baseline, int currentLevel) {
        if (currentLevel <= baseline) {
            return new Result(false, currentLevel);
        }
        boolean crossedMilestone = Math.floorDiv(currentLevel, MILESTONE_INTERVAL)
                > Math.floorDiv(baseline, MILESTONE_INTERVAL);
        return new Result(crossedMilestone, currentLevel);
    }

    public record Result(boolean triggered, int newBaseline) {
    }
}
