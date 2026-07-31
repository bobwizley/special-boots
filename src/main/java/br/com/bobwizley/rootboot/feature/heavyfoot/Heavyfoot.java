package br.com.bobwizley.rootboot.feature.heavyfoot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class Heavyfoot {

    private static boolean enabled;
    private static int radius;

    private Heavyfoot() {
    }

    static void enable(int configuredRadius) {
        enabled = true;
        radius = configuredRadius;
    }

    static void disable() {
        enabled = false;
    }

    /**
     * Square area centered on the block the player occupies, preserved literally from the
     * reference: radius 0 covers 1×1, 1 covers 3×3 and 2 covers 5×5.
     */
    public static void trample(ServerLevel level, Entity entity) {
        if (!enabled || !(entity instanceof Player player)) {
            return;
        }

        BlockPos center = entity.blockPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                flatten(level, player, center.offset(x, -1, z));

                BlockPos feet = center.offset(x, 0, z);
                if (!destroyVegetation(level, player, feet)) {
                    destroyVegetation(level, player, feet.above());
                }
            }
        }
    }

    private static void flatten(ServerLevel level, Player player, BlockPos pos) {
        if (isFlattenable(level.getBlockState(pos)) && mayChange(level, player, pos)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT_PATH.defaultBlockState());
        }
    }

    private static boolean destroyVegetation(ServerLevel level, Player player, BlockPos pos) {
        if (!isDestructible(level.getBlockState(pos)) || !mayChange(level, player, pos)) {
            return false;
        }

        return level.destroyBlock(pos, true, player);
    }

    /**
     * The effect changes blocks directly instead of going through the player's break flow, so
     * the rules that flow would apply — spawn protection, world border, spectator and adventure
     * restrictions — have to be checked here.
     */
    private static boolean mayChange(ServerLevel level, Player player, BlockPos pos) {
        return level.mayInteract(player, pos)
                && !player.blockActionRestricted(level, pos, player.gameMode());
    }

    private static boolean isFlattenable(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK
                || block == Blocks.DIRT
                || block == Blocks.COARSE_DIRT
                || block == Blocks.PODZOL
                || block == Blocks.MYCELIUM
                || block == Blocks.ROOTED_DIRT;
    }

    private static boolean isDestructible(BlockState state) {
        if (state.is(BlockTags.FLOWERS)) {
            return true;
        }

        Block block = state.getBlock();
        return block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || block == Blocks.DEAD_BUSH
                || block == Blocks.SWEET_BERRY_BUSH;
    }
}
