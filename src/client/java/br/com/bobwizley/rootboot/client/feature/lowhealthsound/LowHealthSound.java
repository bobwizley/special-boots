package br.com.bobwizley.rootboot.client.feature.lowhealthsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class LowHealthSound {

    public static final float THRESHOLD = 8.0F;

    private static final float VOLUME = 0.5F;
    private static final float PITCH = 1.0F;

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
        if (!enabled || !beats(previousHealth, newHealth)) {
            return;
        }

        // Attached to the listener rather than to a position, so nobody else can hear it.
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
                SoundEvents.WARDEN_HEARTBEAT.location(), SoundSource.PLAYERS, VOLUME, PITCH,
                SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0, true));
    }
}
