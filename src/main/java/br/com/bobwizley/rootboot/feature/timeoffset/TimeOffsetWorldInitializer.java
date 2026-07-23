package br.com.bobwizley.rootboot.feature.timeoffset;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;

/**
 * Applies the one-shot Time Offset to a world on server start. The decision itself lives in
 * {@link TimeOffsetDecision}; this class only reads/writes the persistent state and advances
 * the overworld clock by one day, mirroring vanilla {@code /time add}.
 */
public final class TimeOffsetWorldInitializer {

    private TimeOffsetWorldInitializer() {
    }

    public static void firstInit(MinecraftServer server, boolean featureEnabled) {
        TimeOffsetState state = server.overworld().getDataStorage().computeIfAbsent(TimeOffsetState.TYPE);
        boolean worldFreshlyCreated = server.overworld().getLevelData().getGameTime() == 0L;
        TimeOffsetDecision decision =
                TimeOffsetDecision.evaluate(state.isEvaluated(), featureEnabled, worldFreshlyCreated);

        if (decision.ticksToAdd() > 0L) {
            Holder<WorldClock> overworldClock = server.registryAccess()
                    .lookupOrThrow(Registries.WORLD_CLOCK)
                    .getOrThrow(WorldClocks.OVERWORLD);
            server.clockManager().addTicks(overworldClock, (int) decision.ticksToAdd());
        }
        if (decision.shouldMarkEvaluated()) {
            state.markEvaluated();
        }
    }
}
