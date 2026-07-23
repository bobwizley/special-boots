package br.com.bobwizley.rootboot.feature.timeoffset;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Per-world persistence for the one-shot Time Offset evaluation. It records whether the
 * decision was already taken so that later enabling, disabling or restarts never re-apply
 * nor revert the offset. The flag is persisted even when the offset was not applied.
 */
public final class TimeOffsetState extends SavedData {

    public static final SavedDataType<TimeOffsetState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("rootboot", "time_offset"),
            () -> new TimeOffsetState(false),
            Codec.BOOL.fieldOf("evaluated").xmap(TimeOffsetState::new, TimeOffsetState::isEvaluated).codec(),
            DataFixTypes.LEVEL);

    private boolean evaluated;

    public TimeOffsetState(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void markEvaluated() {
        if (!evaluated) {
            evaluated = true;
            setDirty();
        }
    }
}
