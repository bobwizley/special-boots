package br.com.bobwizley.rootboot.feature.levelmilestone;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Per-player, per-world persistence for the Level Milestone baseline. The baseline is the last
 * experience level observed for a player, silently reset on every login or reconnect so that
 * offline level changes (including a re-enable of the feature) never retroactively celebrate.
 */
public final class LevelMilestoneState extends SavedData {

    public static final SavedDataType<LevelMilestoneState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("rootboot", "level_milestone"),
            () -> new LevelMilestoneState(new HashMap<>()),
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT)
                    .fieldOf("baselines")
                    .xmap(baselines -> new LevelMilestoneState(new HashMap<>(baselines)),
                            state -> state.baselines)
                    .codec(),
            DataFixTypes.LEVEL);

    private final Map<UUID, Integer> baselines;

    private LevelMilestoneState(Map<UUID, Integer> baselines) {
        this.baselines = baselines;
    }

    public Integer baseline(UUID playerId) {
        return baselines.get(playerId);
    }

    public void setBaseline(UUID playerId, int level) {
        Integer current = baselines.get(playerId);
        if (current == null || current != level) {
            baselines.put(playerId, level);
            setDirty();
        }
    }
}
