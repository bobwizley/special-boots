package br.com.bobwizley.rootboot.feature.cropsexperience;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The randomness stays controlled without touching the roll: the eligibility cases return
 * {@link CropsExperience#NO_CHANCE} whatever the roll is, and the gourds' 100% always beats a
 * roll drawn from {@code [0, 1)}, so every assertion below is deterministic.
 */
public final class CropsExperienceGameTests {

    private static final BlockPos HARVESTED = new BlockPos(1, 2, 1);

    @GameTest
    public void matureCommonCropsRollSeventyFivePercent(GameTestHelper helper) {
        assertChance(helper, CropsExperience.CROP_CHANCE, mature(Blocks.WHEAT), bareHand());
        assertChance(helper, CropsExperience.CROP_CHANCE, mature(Blocks.CARROTS), bareHand());
        assertChance(helper, CropsExperience.CROP_CHANCE, mature(Blocks.POTATOES), bareHand());
        assertChance(helper, CropsExperience.CROP_CHANCE, matureBeetroots(), bareHand());
        assertChance(helper, CropsExperience.CROP_CHANCE, matureCocoa(), bareHand());
        assertChance(helper, CropsExperience.CROP_CHANCE, matureNetherWart(), bareHand());
        helper.succeed();
    }

    @GameTest
    public void melonAndPumpkinAlwaysReward(GameTestHelper helper) {
        assertChance(
                helper,
                CropsExperience.GOURD_CHANCE,
                Blocks.MELON.defaultBlockState(),
                bareHand());
        assertChance(
                helper,
                CropsExperience.GOURD_CHANCE,
                Blocks.PUMPKIN.defaultBlockState(),
                bareHand());
        helper.succeed();
    }

    @GameTest
    public void immaturePlantsAreNeverEligible(GameTestHelper helper) {
        assertChance(helper, CropsExperience.NO_CHANCE, growing(Blocks.WHEAT), bareHand());
        assertChance(
                helper,
                CropsExperience.NO_CHANCE,
                Blocks.BEETROOTS.defaultBlockState().setValue(BeetrootBlock.AGE, 2),
                bareHand());
        assertChance(
                helper,
                CropsExperience.NO_CHANCE,
                Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, 1),
                bareHand());
        assertChance(
                helper,
                CropsExperience.NO_CHANCE,
                Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 2),
                bareHand());
        helper.succeed();
    }

    @GameTest
    public void silkTouchHarvestsAreNeverEligible(GameTestHelper helper) {
        ItemStack silkTouch = enchanted(helper, Enchantments.SILK_TOUCH);
        assertChance(helper, CropsExperience.NO_CHANCE, mature(Blocks.WHEAT), silkTouch);
        assertChance(
                helper, CropsExperience.NO_CHANCE, Blocks.MELON.defaultBlockState(), silkTouch);
        helper.succeed();
    }

    @GameTest
    public void fortuneChangesNeitherEligibilityNorChance(GameTestHelper helper) {
        ItemStack fortune = enchanted(helper, Enchantments.FORTUNE);
        assertChance(helper, CropsExperience.CROP_CHANCE, mature(Blocks.WHEAT), fortune);
        assertChance(
                helper, CropsExperience.GOURD_CHANCE, Blocks.MELON.defaultBlockState(), fortune);
        assertChance(helper, CropsExperience.NO_CHANCE, growing(Blocks.WHEAT), fortune);
        helper.succeed();
    }

    @GameTest
    public void blocksOutsideTheRewardAreNeverEligible(GameTestHelper helper) {
        assertChance(
                helper, CropsExperience.NO_CHANCE, Blocks.DIRT.defaultBlockState(), bareHand());
        assertChance(
                helper,
                CropsExperience.NO_CHANCE,
                Blocks.CARVED_PUMPKIN.defaultBlockState(),
                bareHand());
        helper.succeed();
    }

    /** Fortune is in hand to prove the reward neither scales nor rolls twice. */
    @GameTest
    public void aHarvestAwardsExactlyOneOrbWorthOnePoint(GameTestHelper helper) {
        harvest(helper, Blocks.MELON.defaultBlockState(), enchanted(helper, Enchantments.FORTUNE));

        List<ExperienceOrb> orbs = orbs(helper);
        helper.assertTrue(
                orbs.size() == 1,
                "A rewarded harvest must award a single orb (found " + orbs.size() + ")");
        helper.assertTrue(
                orbs.getFirst().getValue() == 1,
                "The orb must be worth one point (found " + orbs.getFirst().getValue() + ")");
        helper.succeed();
    }

    @GameTest
    public void anEnvironmentalBlockChangeAwardsNothing(GameTestHelper helper) {
        helper.setBlock(HARVESTED, Blocks.MELON);
        helper.getLevel().destroyBlock(helper.absolutePos(HARVESTED), false);

        assertNoOrbs(helper, "Only a player's own harvest may award experience");
        helper.succeed();
    }

    @GameTest
    public void aDisabledFeatureAwardsNothing(GameTestHelper helper) {
        CropsExperience.disable();
        try {
            harvest(helper, Blocks.MELON.defaultBlockState(), bareHand());
            assertNoOrbs(helper, "A disabled feature must award nothing");
        } finally {
            CropsExperience.enable();
        }
        helper.succeed();
    }

    private static void harvest(GameTestHelper helper, BlockState state, ItemStack tool) {
        BlockPos absolute = helper.absolutePos(HARVESTED);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        state.getBlock()
                .playerDestroy(helper.getLevel(), player, absolute, state, null, tool);
    }

    private static void assertChance(
            GameTestHelper helper, float expected, BlockState state, ItemStack tool) {
        float actual = CropsExperience.chance(registries(helper), state, tool);
        helper.assertTrue(
                actual == expected,
                state.getBlock().getName().getString()
                        + " must roll " + expected + " (found " + actual + ")");
    }

    private static void assertNoOrbs(GameTestHelper helper, String message) {
        List<ExperienceOrb> orbs = orbs(helper);
        helper.assertTrue(orbs.isEmpty(), message + " (found " + orbs.size() + " orb(s))");
    }

    private static List<ExperienceOrb> orbs(GameTestHelper helper) {
        return helper.getLevel()
                .getEntitiesOfClass(
                        ExperienceOrb.class,
                        AABB.ofSize(Vec3.atCenterOf(helper.absolutePos(HARVESTED)), 8.0, 8.0, 8.0));
    }

    private static BlockState mature(Block crop) {
        return crop.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE);
    }

    private static BlockState growing(Block crop) {
        return crop.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE - 1);
    }

    private static BlockState matureBeetroots() {
        return Blocks.BEETROOTS.defaultBlockState()
                .setValue(BeetrootBlock.AGE, BeetrootBlock.MAX_AGE);
    }

    private static BlockState matureCocoa() {
        return Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, CocoaBlock.MAX_AGE);
    }

    private static BlockState matureNetherWart() {
        return Blocks.NETHER_WART.defaultBlockState()
                .setValue(NetherWartBlock.AGE, NetherWartBlock.MAX_AGE);
    }

    private static ItemStack bareHand() {
        return ItemStack.EMPTY;
    }

    private static ItemStack enchanted(GameTestHelper helper, ResourceKey<Enchantment> enchantment) {
        ItemStack tool = Items.DIAMOND_HOE.getDefaultInstance();
        tool.enchant(
                registries(helper).lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment),
                1);
        return tool;
    }

    private static RegistryAccess registries(GameTestHelper helper) {
        return helper.getLevel().registryAccess();
    }
}
