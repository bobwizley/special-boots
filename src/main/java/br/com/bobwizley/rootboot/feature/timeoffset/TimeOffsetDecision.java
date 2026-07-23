package br.com.bobwizley.rootboot.feature.timeoffset;

/**
 * The one-shot, per-world Time Offset evaluation, isolated from Minecraft so it can be
 * unit-tested. It runs only on a world's first initialization (guarded by
 * {@code alreadyEvaluated}): it adds exactly one day iff the feature is enabled AND the world
 * was freshly created, and always marks the world as evaluated so later enabling, disabling
 * or restarts never apply nor revert the offset. Requiring a freshly created world keeps
 * pre-existing worlds (including any created before RootBoot was installed) untouched.
 */
public final class TimeOffsetDecision {

    public static final long ONE_DAY_TICKS = 24000L;

    private final long ticksToAdd;
    private final boolean shouldMarkEvaluated;

    private TimeOffsetDecision(long ticksToAdd, boolean shouldMarkEvaluated) {
        this.ticksToAdd = ticksToAdd;
        this.shouldMarkEvaluated = shouldMarkEvaluated;
    }

    public long ticksToAdd() {
        return ticksToAdd;
    }

    public boolean shouldMarkEvaluated() {
        return shouldMarkEvaluated;
    }

    public static TimeOffsetDecision evaluate(boolean alreadyEvaluated, boolean featureEnabled,
            boolean worldFreshlyCreated) {
        if (alreadyEvaluated) {
            return new TimeOffsetDecision(0L, false);
        }
        long ticksToAdd = (featureEnabled && worldFreshlyCreated) ? ONE_DAY_TICKS : 0L;
        return new TimeOffsetDecision(ticksToAdd, true);
    }
}
