package br.com.bobwizley.rootboot.client.feature.stopmusicondeath;

import br.com.bobwizley.rootboot.feature.Feature;

public final class StopMusicOnDeathFeature implements Feature {

    public static final String ID = "stop_music_on_death";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        StopMusicOnDeath.enable();
    }
}
