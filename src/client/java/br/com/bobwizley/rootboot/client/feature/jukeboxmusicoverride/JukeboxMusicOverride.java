package br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.JukeboxSong;

public final class JukeboxMusicOverride {

    private static final AudibleJukeboxes JUKEBOXES = new AudibleJukeboxes();

    private static boolean enabled;
    private static boolean ambientMusicInterrupted;
    private static ClientLevel trackedLevel;

    private JukeboxMusicOverride() {
    }

    static void enable() {
        enabled = true;
    }

    public static void discStarted(BlockPos pos, JukeboxSong song, long gameTime) {
        if (enabled) {
            JUKEBOXES.started(pos, song.lengthInTicks(), gameTime);
        }
    }

    public static void discStopped(BlockPos pos) {
        if (enabled) {
            JUKEBOXES.stopped(pos);
        }
    }

    /**
     * Interrupts the ambient track the first tick a jukebox becomes audible and reports whether
     * the vanilla scheduler must stay parked this tick. Parking it, rather than muting the
     * track, is what keeps the suppression independent of the jukebox category volume.
     */
    public static boolean parksAmbientMusicScheduler(MusicManager manager) {
        if (!enabled) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        forgetJukeboxesFromOtherLevels(level);
        if (level == null || player == null
                || !JUKEBOXES.anyAudibleFrom(player.position(), level.getGameTime())) {
            ambientMusicInterrupted = false;
            return false;
        }

        if (!ambientMusicInterrupted) {
            manager.stopPlaying();
            ambientMusicInterrupted = true;
        }
        return true;
    }

    /**
     * Positions from a level the client has left would keep matching by coordinate alone, and
     * that level's stop events can no longer arrive.
     */
    private static void forgetJukeboxesFromOtherLevels(ClientLevel level) {
        if (level != trackedLevel) {
            trackedLevel = level;
            JUKEBOXES.clear();
        }
    }
}
