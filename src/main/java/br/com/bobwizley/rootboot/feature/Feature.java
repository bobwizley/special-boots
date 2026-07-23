package br.com.bobwizley.rootboot.feature;

/**
 * A togglable unit of behavior known to the {@link FeatureRegistry}. Each feature has a
 * stable id used both as its config key and as its identity across restarts. Its handlers
 * are installed by {@link #register()}, which the registry invokes only when the feature is
 * enabled at initialization.
 */
public interface Feature {

    String id();

    void register();
}
