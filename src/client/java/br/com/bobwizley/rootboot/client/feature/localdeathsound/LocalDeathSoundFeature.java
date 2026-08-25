package br.com.bobwizley.rootboot.client.feature.localdeathsound;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Improved/Local Death Sound: only the dead player hears a sound chosen by the damage type.
 */
public final class LocalDeathSoundFeature implements Feature {

    public static final String ID = "local_death_sound";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        LocalDeathSound.enable();
    }
}
