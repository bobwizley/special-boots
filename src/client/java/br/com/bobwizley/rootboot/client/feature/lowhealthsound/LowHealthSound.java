package br.com.bobwizley.rootboot.client.feature.lowhealthsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public final class LowHealthSound {

    public static final float THRESHOLD = 8.0F;

    private static final float VOLUME = 0.5F;

    private static boolean enabled;

    private LowHealthSound() {
    }

    static void enable() {
        enabled = true;
    }

    /**
     * Absorption is held outside the health value, so requiring the health itself to drop is
     * what keeps a fully absorbed hit silent and keeps absorption hearts from raising the bar.
     */
    public static boolean beats(float previousHealth, float newHealth) {
        return newHealth < previousHealth && newHealth > 0.0F && newHealth <= THRESHOLD;
    }

    public static void healthChanged(float previousHealth, float newHealth) {
        if (enabled && beats(previousHealth, newHealth)) {
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forUI(SoundEvents.WARDEN_HEARTBEAT, VOLUME));
        }
    }
}
