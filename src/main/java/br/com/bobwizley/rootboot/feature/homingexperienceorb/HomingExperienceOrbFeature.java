package br.com.bobwizley.rootboot.feature.homingexperienceorb;

import br.com.bobwizley.rootboot.feature.Feature;

public final class HomingExperienceOrbFeature implements Feature {

    public static final String ID = "homing_experience_orb";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        HomingExperienceOrbMovement.enable();
    }
}
