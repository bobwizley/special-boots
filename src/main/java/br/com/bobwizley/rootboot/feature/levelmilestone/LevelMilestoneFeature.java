package br.com.bobwizley.rootboot.feature.levelmilestone;

import br.com.bobwizley.rootboot.feature.Feature;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Level Milestone: celebrates an ascending crossing of a multiple-of-5 experience level with a
 * particle burst visible only to that player. Detection and the baseline lifecycle live in
 * {@link LevelMilestoneTracker} and {@link LevelMilestoneState}; login and reconnection always
 * rebase silently so no offline change, first init or feature re-enable can retroactively
 * celebrate.
 */
public final class LevelMilestoneFeature implements Feature {

    public static final String ID = "level_milestone";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            state(server).setBaseline(player.getUUID(), player.experienceLevel);
        });
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        LevelMilestoneState state = state(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Integer baseline = state.baseline(player.getUUID());
            if (baseline == null) {
                state.setBaseline(player.getUUID(), player.experienceLevel);
                continue;
            }
            LevelMilestoneTracker.Result result =
                    LevelMilestoneTracker.evaluate(baseline, player.experienceLevel);
            if (result.triggered()) {
                present(player);
            }
            state.setBaseline(player.getUUID(), result.newBaseline());
        }
    }

    private static void present(ServerPlayer player) {
        player.level().sendParticles(
                player,
                ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS,
                true,
                true,
                player.getX(),
                player.getY() + 1.5,
                player.getZ(),
                80,
                0.7,
                0.6,
                0.7,
                0.02);

        SpellParticleOption effect = SpellParticleOption.create(ParticleTypes.EFFECT, -1, 1.0F);
        Vec3 origin = player.position().add(0.0, 1.0, 0.0);
        for (int step = 0; step < 72; step++) {
            Vec3 offset = Vec3.directionFromRotation(
                    player.getXRot() + (20.0F * step),
                    player.getYRot() + (5.0F * step)).scale(1.5);
            Vec3 position = origin.add(offset);
            player.level().sendParticles(
                    player,
                    effect,
                    true,
                    true,
                    position.x,
                    position.y,
                    position.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.01);
        }
    }

    private static LevelMilestoneState state(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(LevelMilestoneState.TYPE);
    }
}
