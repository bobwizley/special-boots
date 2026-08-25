package br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * The jukeboxes the client has been told to play, each with the tick its disc runs out. The
 * client is only informed that a disc started or stopped while it is within the level event
 * radius, so an entry whose stop was never delivered is discarded once its disc would have
 * ended instead of suppressing music forever.
 */
public final class AudibleJukeboxes {

    /**
     * Jukebox discs play at volume 4 with linear attenuation, which is 16 blocks per volume
     * point, so the client mixes them in up to 64 blocks away.
     */
    public static final double AUDIBLE_RANGE = 64.0;

    private final Map<BlockPos, Long> playingUntil = new HashMap<>();

    public void started(BlockPos pos, int lengthInTicks, long gameTime) {
        playingUntil.put(pos.immutable(), gameTime + lengthInTicks);
    }

    public void stopped(BlockPos pos) {
        playingUntil.remove(pos);
    }

    public void clear() {
        playingUntil.clear();
    }

    public boolean anyAudibleFrom(Vec3 listener, long gameTime) {
        playingUntil.values().removeIf(until -> until <= gameTime);
        return playingUntil.keySet().stream().anyMatch(pos ->
                Vec3.atCenterOf(pos).distanceToSqr(listener) <= AUDIBLE_RANGE * AUDIBLE_RANGE);
    }
}
