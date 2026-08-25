package br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

class AudibleJukeboxesTest {

    private static final BlockPos ORIGIN = BlockPos.ZERO;
    private static final Vec3 LISTENER = Vec3.ZERO;
    private static final int SONG_LENGTH = 200;

    @Test
    void reportsAJukeboxStartedWithinRange() {
        AudibleJukeboxes jukeboxes = new AudibleJukeboxes();
        jukeboxes.started(ORIGIN, SONG_LENGTH, 0L);

        assertTrue(jukeboxes.anyAudibleFrom(LISTENER, 0L));
        assertTrue(jukeboxes.anyAudibleFrom(LISTENER, SONG_LENGTH - 1L));
    }

    @Test
    void ignoresAJukeboxThatWasStopped() {
        AudibleJukeboxes jukeboxes = new AudibleJukeboxes();
        jukeboxes.started(ORIGIN, SONG_LENGTH, 0L);
        jukeboxes.stopped(ORIGIN);

        assertFalse(jukeboxes.anyAudibleFrom(LISTENER, 0L));
    }

    @Test
    void ignoresAJukeboxWhoseDiscHasRunOut() {
        AudibleJukeboxes jukeboxes = new AudibleJukeboxes();
        jukeboxes.started(ORIGIN, SONG_LENGTH, 0L);

        assertFalse(jukeboxes.anyAudibleFrom(LISTENER, SONG_LENGTH));
    }

    @Test
    void ignoresAJukeboxBeyondTheAudibleRange() {
        AudibleJukeboxes jukeboxes = new AudibleJukeboxes();
        int outOfRange = (int) AudibleJukeboxes.AUDIBLE_RANGE + 1;
        jukeboxes.started(new BlockPos(outOfRange, 0, 0), SONG_LENGTH, 0L);

        assertFalse(jukeboxes.anyAudibleFrom(LISTENER, 0L));
        assertTrue(jukeboxes.anyAudibleFrom(new Vec3(outOfRange, 0.0, 0.0), 0L));
    }

    @Test
    void clearForgetsEveryJukebox() {
        AudibleJukeboxes jukeboxes = new AudibleJukeboxes();
        jukeboxes.started(ORIGIN, SONG_LENGTH, 0L);
        jukeboxes.clear();

        assertFalse(jukeboxes.anyAudibleFrom(LISTENER, 0L));
    }
}
