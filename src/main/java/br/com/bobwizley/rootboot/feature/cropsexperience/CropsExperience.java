package br.com.bobwizley.rootboot.feature.cropsexperience;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rewards an eligible harvest with a single experience orb. The odds diverge deliberately from
 * both the 20%/40% the Vanilla Refresh documents and the 50%/100% it actually rolls.
 */
public final class CropsExperience {

    public static final float NO_CHANCE = 0.0F;
    public static final float CROP_CHANCE = 0.75F;
    public static final float GOURD_CHANCE = 1.0F;

    private static final int ORB_VALUE = 1;

    private static final Map<Block, Float> CHANCES = Map.of(
            Blocks.WHEAT, CROP_CHANCE,
            Blocks.CARROTS, CROP_CHANCE,
            Blocks.POTATOES, CROP_CHANCE,
            Blocks.BEETROOTS, CROP_CHANCE,
            Blocks.COCOA, CROP_CHANCE,
            Blocks.NETHER_WART, CROP_CHANCE,
            Blocks.MELON, GOURD_CHANCE,
            Blocks.PUMPKIN, GOURD_CHANCE);

    private static boolean enabled;

    private CropsExperience() {
    }

    static void enable() {
        enabled = true;
    }

    static void disable() {
        enabled = false;
    }

    public static void harvest(Level level, BlockPos pos, BlockState state, ItemStack tool) {
        if (!enabled || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        float chance = chance(serverLevel.registryAccess(), state, tool);
        if (chance <= NO_CHANCE || serverLevel.getRandom().nextFloat() >= chance) {
            return;
        }

        serverLevel.addFreshEntity(new ExperienceOrb(
                serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ORB_VALUE));
    }

    /**
     * The odds of the single roll the harvest is worth, or {@link #NO_CHANCE} when the harvest is
     * not eligible. Fortune is absent on purpose: it changes neither eligibility nor the odds.
     */
    public static float chance(RegistryAccess registries, BlockState state, ItemStack tool) {
        Float chance = CHANCES.get(state.getBlock());
        if (chance == null || !isFullyGrown(state) || hasSilkTouch(registries, tool)) {
            return NO_CHANCE;
        }
        return chance;
    }

    private static boolean isFullyGrown(BlockState state) {
        if (state.getBlock() instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (state.is(Blocks.COCOA)) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }
        if (state.is(Blocks.NETHER_WART)) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        }
        // Melon and pumpkin have no growth stage: reaching the block means it is ripe.
        return true;
    }

    private static boolean hasSilkTouch(RegistryAccess registries, ItemStack tool) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT)
                .get(Enchantments.SILK_TOUCH)
                .map(silkTouch -> EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0)
                .orElse(false);
    }
}
