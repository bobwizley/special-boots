package br.com.bobwizley.rootboot.feature.heavyfoot;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Heavyfoot: boots that flatten soil into dirt path and destroy small vegetation around the
 * wearer. The configured radius is captured at initialization, so changing it takes effect only
 * after a restart, like every other feature setting.
 */
public final class HeavyfootFeature implements Feature {
    public static final String ID = "heavyfoot";

    private final int radius;

    public HeavyfootFeature(int radius) {
        this.radius = radius;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        Heavyfoot.enable(radius);
    }
}
