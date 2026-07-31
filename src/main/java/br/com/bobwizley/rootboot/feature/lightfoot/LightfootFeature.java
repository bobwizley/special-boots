package br.com.bobwizley.rootboot.feature.lightfoot;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Lightfoot: boots that keep farmland from being trampled into dirt by the wearer's fall.
 */
public final class LightfootFeature implements Feature {
    public static final String ID = "lightfoot";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        Lightfoot.enable();
    }
}
