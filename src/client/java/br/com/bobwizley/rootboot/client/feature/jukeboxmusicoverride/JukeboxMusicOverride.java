package br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

    public static void discStarted(BlockPos pos, JukeboxSong song, ClientLevel level) {
        if (enabled) {
            jukeboxesOf(level).started(pos, song.lengthInTicks(), level.getGameTime());
        }
    }

    public static void discStopped(BlockPos pos, ClientLevel level) {
        if (enabled) {
            jukeboxesOf(level).stopped(pos);
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
        if (level == null || !jukeboxesOf(level).anyAudibleFrom(listenerPosition(), level.getGameTime())) {
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
     * The jukebox is mixed in relative to the audio listener, which is the camera and not the
     * player: in spectator or with a detached camera the two are far apart.
     */
    private static net.minecraft.world.phys.Vec3 listenerPosition() {
        return Minecraft.getInstance().getSoundManager().getListenerTransform().position();
    }

    /**
     * Positions from a level the client has left would keep matching by coordinate alone, and
     * that level's stop events can no longer arrive. The check runs on the recording path too,
     * because a start event for the new level can arrive before the next music tick.
     */
    private static AudibleJukeboxes jukeboxesOf(ClientLevel level) {
        if (level != trackedLevel) {
            trackedLevel = level;
            JUKEBOXES.clear();
        }
        return JUKEBOXES;
    }
}
