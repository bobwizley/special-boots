package br.com.bobwizley.rootboot.feature.halfhealthbabies;

import br.com.bobwizley.rootboot.feature.Feature;

public final class HalfHealthBabiesFeature implements Feature {

    public static final String ID = "half_health_babies";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        HalfHealthBabies.enable();
    }
}
