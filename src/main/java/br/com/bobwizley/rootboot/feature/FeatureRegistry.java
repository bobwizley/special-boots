package br.com.bobwizley.rootboot.feature;

import br.com.bobwizley.rootboot.config.RootBootConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Central point where every behavior feature is registered with its stable id. During
 * initialization the registry consults the config and installs only the handlers of the
 * features whose toggle is enabled; disabled features register nothing.
 */
public final class FeatureRegistry {

    private final List<Feature> features;

    public FeatureRegistry(List<Feature> features) {
        this.features = List.copyOf(features);
    }

    public List<Feature> features() {
        return features;
    }

    public List<String> featureIds() {
        return features.stream().map(Feature::id).toList();
    }

    public List<String> registerEnabled(RootBootConfig config) {
        List<String> registered = new ArrayList<>();
        for (Feature feature : features) {
            if (config.isEnabled(feature.id())) {
                feature.register();
                registered.add(feature.id());
            }
        }
        return registered;
    }
}
