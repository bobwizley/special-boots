package br.com.bobwizley.rootboot.feature.deathitemprotection;

import br.com.bobwizley.rootboot.feature.Feature;

public final class DeathItemProtectionFeature implements Feature {

    public static final String ID = "death_items_dont_despawn";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        DeathItemProtection.enable();
    }
}
