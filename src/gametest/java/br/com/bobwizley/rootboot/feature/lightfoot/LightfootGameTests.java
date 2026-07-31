package br.com.bobwizley.rootboot.feature.lightfoot;

import br.com.bobwizley.rootboot.enchantment.RootBootEnchantments;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;

public final class LightfootGameTests {

    private static final BlockPos FARMLAND = new BlockPos(3, 3, 3);

    /** Always above the 0.5 threshold {@code fallOn} rolls against, so trampling never depends on chance. */
    private static final double FALL_DISTANCE = 5.0;

    @GameTest
    public void lightfootBootsKeepFarmlandIntact(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.FEET, lightfootBoots(helper));

        Lightfoot.enable();
        fallOnFarmland(helper, player);

        assertBlockIs(helper, Blocks.FARMLAND, "Lightfoot must keep the farmland intact");
        finish(helper, player);
    }

    @GameTest
    public void bootsWithoutTheEnchantmentTrampleAsInVanilla(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.FEET, Items.LEATHER_BOOTS.getDefaultInstance());

        Lightfoot.enable();
        fallOnFarmland(helper, player);

        assertBlockIs(helper, Blocks.DIRT, "Boots without Lightfoot must trample");
        finish(helper, player);
    }

    @GameTest
    public void aDisabledFeatureSuspendsTheEffect(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.FEET, lightfootBoots(helper));

        Lightfoot.disable();
        fallOnFarmland(helper, player);

        assertBlockIs(helper, Blocks.DIRT, "A disabled feature must restore vanilla trampling");
        Lightfoot.enable();
        finish(helper, player);
    }

    @GameTest
    public void theEnchantmentDoesNotProtectFromAnIneligibleSlot(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.MAINHAND, lightfootBoots(helper));

        Lightfoot.enable();
        fallOnFarmland(helper, player);

        assertBlockIs(helper, Blocks.DIRT, "Enchanted boots held in hand must not protect");
        finish(helper, player);
    }

    @GameTest
    public void protectingTheFarmlandDoesNotCancelTheFall(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.FEET, lightfootBoots(helper));

        Lightfoot.enable();
        fallOnFarmland(helper, player);

        assertBlockIs(helper, Blocks.FARMLAND, "Lightfoot must keep the farmland intact");
        int fallenCentimeters =
                player.getStats().getValue(Stats.CUSTOM.get(Stats.FALL_ONE_CM));
        if (fallenCentimeters != (int) (FALL_DISTANCE * 100)) {
            helper.fail(Component.literal(
                    "The protected fall must still reach the vanilla fall handling"
                            + " (fallen centimeters: " + fallenCentimeters + ")"));
        }
        finish(helper, player);
    }

    /** The redirect targets a single call site, so farmland conversion itself stays available. */
    @GameTest
    public void otherFarmlandConversionsStillApplyToTheWearer(GameTestHelper helper) {
        ServerPlayer player = playerOnFarmland(helper);
        player.setItemSlot(EquipmentSlot.FEET, lightfootBoots(helper));

        Lightfoot.enable();
        BlockPos absolute = helper.absolutePos(FARMLAND);
        FarmlandBlock.turnToDirt(
                player, helper.getBlockState(FARMLAND), helper.getLevel(), absolute);

        assertBlockIs(helper, Blocks.DIRT, "Farmland must not become globally immune");
        finish(helper, player);
    }

    private static void fallOnFarmland(GameTestHelper helper, ServerPlayer player) {
        BlockPos absolute = helper.absolutePos(FARMLAND);
        Blocks.FARMLAND.fallOn(
                helper.getLevel(),
                helper.getBlockState(FARMLAND),
                absolute,
                player,
                FALL_DISTANCE);
    }

    private static ItemStack lightfootBoots(GameTestHelper helper) {
        ItemStack boots = Items.LEATHER_BOOTS.getDefaultInstance();
        boots.enchant(
                helper.getLevel()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(RootBootEnchantments.LIGHTFOOT),
                1);
        return boots;
    }

    private static ServerPlayer playerOnFarmland(GameTestHelper helper) {
        helper.setBlock(FARMLAND, Blocks.FARMLAND);
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        // Without this the mock player keeps the creative abilities it was placed with, and mayfly
        // short-circuits the vanilla fall handling the protected path must still reach.
        GameType.SURVIVAL.updatePlayerAbilities(player.getAbilities());
        BlockPos absolute = helper.absolutePos(FARMLAND.above());
        player.setPosRaw(absolute.getX() + 0.5, absolute.getY(), absolute.getZ() + 0.5);
        return player;
    }

    private static void assertBlockIs(GameTestHelper helper, Block expected, String message) {
        helper.assertBlock(
                FARMLAND,
                block -> block == expected,
                found -> Component.literal(
                        message + " (expected " + expected + ", found " + found + ")"));
    }

    private static void finish(GameTestHelper helper, ServerPlayer player) {
        helper.getLevel().getServer().getPlayerList().remove(player);
        helper.succeed();
    }
}
