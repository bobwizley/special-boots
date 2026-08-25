package br.com.bobwizley.rootboot.client.feature.stopmusicondeath;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class StopMusicOnDeath {

    private static boolean enabled;

    private StopMusicOnDeath() {
    }

    static void enable() {
        enabled = true;
    }

    /**
     * Stopping the music manager reaches only the ambient track it owns, which is what leaves
     * jukeboxes and every other sound category playing.
     */
    public static void died(LivingEntity entity) {
        if (enabled && entity == Minecraft.getInstance().player) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
    }
}
