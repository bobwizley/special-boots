package br.com.bobwizley.rootboot.feature.lightfoot;

import br.com.bobwizley.rootboot.feature.Feature;

public final class LightfootFeature implements Feature {
    public static final String ID = "lightfoot";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        // Effects to be implemented in a future ticket
    }
}
