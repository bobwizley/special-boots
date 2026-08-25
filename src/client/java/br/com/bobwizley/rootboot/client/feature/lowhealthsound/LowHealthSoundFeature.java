package br.com.bobwizley.rootboot.client.feature.lowhealthsound;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Low Health Sound: only the hurt player hears a beat when a non-fatal hit leaves them at or
 * below four hearts.
 */
public final class LowHealthSoundFeature implements Feature {

    public static final String ID = "low_health_sound";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        LowHealthSound.enable();
    }
}
