package br.com.bobwizley.rootboot.feature.cropsexperience;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
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
 * The roll is driven, never observed: {@link CropsExperience.Roll} lets each case pin the drawn
 * value and count the draws, so the odds, the "single roll" rule and the eligibility gate are all
 * asserted deterministically instead of by sampling a frequency.
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
                helper, CropsExperience.GOURD_CHANCE, Blocks.MELON.defaultBlockState(), bareHand());
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

    @GameTest
    public void theRollDecidesAgainstTheCropChance(GameTestHelper helper) {
        assertRolledHarvest(helper, mature(Blocks.WHEAT), 0.0F, true);
        assertRolledHarvest(helper, mature(Blocks.WHEAT), Math.nextDown(0.75F), true);
        assertRolledHarvest(helper, mature(Blocks.WHEAT), 0.75F, false);
        assertRolledHarvest(helper, mature(Blocks.WHEAT), 0.99F, false);
        helper.succeed();
    }

    /** A roll of {@code [0, 1)} can never reach 1.0, which is what makes the gourds unconditional. */
    @GameTest
    public void theGourdChanceBeatsEveryRoll(GameTestHelper helper) {
        assertRolledHarvest(helper, Blocks.MELON.defaultBlockState(), Math.nextDown(1.0F), true);
        helper.succeed();
    }

    @GameTest
    public void onlyEligibleHarvestsDrawARoll(GameTestHelper helper) {
        assertDraws(helper, mature(Blocks.WHEAT), bareHand(), 1);
        assertDraws(helper, Blocks.MELON.defaultBlockState(), bareHand(), 1);
        assertDraws(helper, growing(Blocks.WHEAT), bareHand(), 0);
        assertDraws(helper, mature(Blocks.WHEAT), enchanted(helper, Enchantments.SILK_TOUCH), 0);
        assertDraws(helper, Blocks.DIRT.defaultBlockState(), bareHand(), 0);
        helper.succeed();
    }

    @GameTest
    public void aDisabledFeatureNeitherRollsNorAwards(GameTestHelper helper) {
        CropsExperience.disable();
        try {
            assertDraws(helper, Blocks.MELON.defaultBlockState(), bareHand(), 0);
            breakMelon(helper, miner(helper, GameType.SURVIVAL, bareHand()));
            assertNoOrbs(helper, "A disabled feature must award nothing");
        } finally {
            CropsExperience.enable();
        }
        helper.succeed();
    }

    /** Fortune is in hand to prove the reward neither scales nor rolls twice. */
    @GameTest
    public void aPlayerBreakAwardsExactlyOneOrbWorthOnePoint(GameTestHelper helper) {
        breakMelon(
                helper,
                miner(helper, GameType.SURVIVAL, enchanted(helper, Enchantments.FORTUNE)));

        List<ExperienceOrb> orbs = orbs(helper);
        helper.assertTrue(
                orbs.size() == 1,
                "A rewarded harvest must award a single orb (found " + orbs.size() + ")");
        helper.assertTrue(
                orbs.getFirst().getValue() == 1,
                "The orb must be worth one point (found " + orbs.getFirst().getValue() + ")");
        helper.succeed();
    }

    /**
     * The tool breaks on this very swing, so reading the player's hand after the break would find
     * no Silk Touch — the reward must still be denied by the copy taken before the damage.
     */
    @GameTest
    public void silkTouchIsReadFromTheToolBeforeItBreaks(GameTestHelper helper) {
        ItemStack silkTouch = enchanted(helper, Enchantments.SILK_TOUCH);
        silkTouch.setDamageValue(silkTouch.getMaxDamage() - 1);
        ServerPlayer player = miner(helper, GameType.SURVIVAL, silkTouch);

        breakMelon(helper, player);

        helper.assertTrue(
                player.getMainHandItem().isEmpty(), "The test needs the tool to break on the swing");
        assertNoOrbs(helper, "Silk Touch must be read from the tool the harvest started with");
        helper.succeed();
    }

    @GameTest
    public void acreativeBreakAwardsNothing(GameTestHelper helper) {
        breakMelon(helper, miner(helper, GameType.CREATIVE, bareHand()));

        assertNoOrbs(helper, "A creative break drops nothing and must award nothing");
        helper.succeed();
    }

    @GameTest
    public void anEnvironmentalBlockChangeAwardsNothing(GameTestHelper helper) {
        helper.setBlock(HARVESTED, Blocks.MELON);
        helper.getLevel().destroyBlock(helper.absolutePos(HARVESTED), false);

        assertNoOrbs(helper, "Only a player's own harvest may award experience");
        helper.succeed();
    }

    private static ServerPlayer miner(GameTestHelper helper, GameType gameType, ItemStack tool) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(gameType);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        return player;
    }

    private static void breakMelon(GameTestHelper helper, ServerPlayer player) {
        helper.setBlock(HARVESTED, Blocks.MELON);
        player.gameMode.destroyBlock(helper.absolutePos(HARVESTED));
    }

    private static void assertRolledHarvest(
            GameTestHelper helper, BlockState state, float roll, boolean awarded) {
        clearOrbs(helper);
        CropsExperience.harvest(
                helper.getLevel(), helper.absolutePos(HARVESTED), state, bareHand(), () -> roll);

        int found = orbs(helper).size();
        helper.assertTrue(
                found == (awarded ? 1 : 0),
                state.getBlock().getName().getString() + " rolled " + roll
                        + " must award " + (awarded ? 1 : 0) + " orb(s) (found " + found + ")");
    }

    private static void assertDraws(
            GameTestHelper helper, BlockState state, ItemStack tool, int expected) {
        clearOrbs(helper);
        CountingRoll roll = new CountingRoll();
        CropsExperience.harvest(
                helper.getLevel(), helper.absolutePos(HARVESTED), state, tool, roll);

        helper.assertTrue(
                roll.draws == expected,
                state.getBlock().getName().getString() + " must draw " + expected
                        + " roll(s) (drew " + roll.draws + ")");
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

    private static void clearOrbs(GameTestHelper helper) {
        orbs(helper).forEach(Entity::discard);
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

    private static final class CountingRoll implements CropsExperience.Roll {

        private int draws;

        @Override
        public float next() {
            draws++;
            return 0.0F;
        }
    }
}
