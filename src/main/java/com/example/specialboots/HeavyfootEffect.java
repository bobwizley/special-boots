package com.example.specialboots;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record HeavyfootEffect(LevelBasedValue radius) implements EnchantmentEntityEffect {

    public static final MapCodec<HeavyfootEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    LevelBasedValue.CODEC.fieldOf("radius").forGetter(HeavyfootEffect::radius)
            ).apply(instance, HeavyfootEffect::new)
    );

    @Override
    public void apply(ServerLevel world, int level, EnchantedItemInUse context, Entity target, Vec3 pos) {
        if (!(target instanceof Player)) return;

        int r = (int) radius.calculate(level);
        BlockPos center = target.blockPosition();

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                BlockPos surfacePos = center.offset(x, -1, z);
                BlockState surfaceState = world.getBlockState(surfacePos);

                if (isFlattenable(surfaceState)) {
                    world.setBlockAndUpdate(surfacePos, Blocks.DIRT_PATH.defaultBlockState());
                }

                BlockPos feetPos = center.offset(x, 0, z);
                BlockState feetState = world.getBlockState(feetPos);

                if (isDestructible(feetState)) {
                    world.destroyBlock(feetPos, true);
                    continue;
                }

                BlockPos headPos = feetPos.above();
                BlockState headState = world.getBlockState(headPos);

                if (isDestructible(headState)) {
                    world.destroyBlock(headPos, true);
                }
            }
        }
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
        if (state.is(BlockTags.FLOWERS)) return true;
        Block block = state.getBlock();
        return block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || block == Blocks.DEAD_BUSH
                || block == Blocks.SWEET_BERRY_BUSH;
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
