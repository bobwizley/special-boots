package br.com.bobwizley.rootboot.client.feature.jukeboxmusicoverride;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Jukebox Music Override: ambient music stays suppressed while an audible jukebox plays a disc.
 */
public final class JukeboxMusicOverrideFeature implements Feature {

    public static final String ID = "jukebox_music_override";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        JukeboxMusicOverride.enable();
    }
}
