package br.com.bobwizley.rootboot.feature.cropsexperience;

import br.com.bobwizley.rootboot.feature.Feature;

/**
 * Crops XP: harvesting a fully grown crop without Silk Touch can drop one experience point.
 */
public final class CropsExperienceFeature implements Feature {

    public static final String ID = "crops_experience";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        CropsExperience.enable();
    }
}
