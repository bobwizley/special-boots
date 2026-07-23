package br.com.bobwizley.rootboot.feature.levelmilestone;

import br.com.bobwizley.rootboot.feature.Feature;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Level Milestone: celebrates an ascending crossing of a multiple-of-5 experience level with a
 * particle burst and sound visible/audible only to that player. Detection and the baseline
 * lifecycle live in {@link LevelMilestoneTracker} and {@link LevelMilestoneState}; login and
 * reconnection always rebase silently so no offline change, first init or feature re-enable can
 * retroactively celebrate.
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
                ParticleTypes.TOTEM_OF_UNDYING,
                true,
                true,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                100,
                0.4,
                0.6,
                0.4,
                0.02);
        player.connection.send(new ClientboundSoundEntityPacket(
                holder(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE),
                SoundSource.MASTER,
                player,
                0.5F,
                1.0F,
                player.getRandom().nextLong()));
    }

    private static Holder<SoundEvent> holder(SoundEvent sound) {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound);
    }

    private static LevelMilestoneState state(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(LevelMilestoneState.TYPE);
    }
}
