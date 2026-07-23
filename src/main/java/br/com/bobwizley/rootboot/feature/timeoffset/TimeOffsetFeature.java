package br.com.bobwizley.rootboot.feature.timeoffset;

import br.com.bobwizley.rootboot.feature.Feature;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Time Offset: on a world's first initialization, adds one day so the world starts on day 1.
 * The day-adding behavior is registered only when the toggle is enabled at init; the
 * evaluated-marking for the disabled case is handled as a persistence exception by
 * {@code RootBoot} (see {@link TimeOffsetWorldInitializer}).
 */
public final class TimeOffsetFeature implements Feature {

    public static final String ID = "time_offset";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                TimeOffsetWorldInitializer.firstInit(server, true));
    }
}
