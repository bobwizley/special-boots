package br.com.bobwizley.rootboot.feature.heavyfoot;

import br.com.bobwizley.rootboot.enchantment.RootBootEnchantments;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;

public final class HeavyfootGameTests {

    private static final BlockPos CENTER = new BlockPos(3, 4, 3);

    @GameTest
    public void radiusZeroAffectsASingleColumn(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        fillSoil(helper, 2);

        trampleWithRadius(helper, player, 0);

        assertRing(helper, 0, Blocks.DIRT_PATH);
        assertRing(helper, 1, Blocks.GRASS_BLOCK);
        assertRing(helper, 2, Blocks.GRASS_BLOCK);
        finish(helper, player);
    }

    @GameTest
    public void radiusOneAffectsThreeByThree(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        fillSoil(helper, 2);

        trampleWithRadius(helper, player, 1);

        assertRing(helper, 0, Blocks.DIRT_PATH);
        assertRing(helper, 1, Blocks.DIRT_PATH);
        assertRing(helper, 2, Blocks.GRASS_BLOCK);
        finish(helper, player);
    }

    @GameTest
    public void radiusTwoAffectsFiveByFive(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        fillSoil(helper, 2);

        trampleWithRadius(helper, player, 2);

        assertRing(helper, 0, Blocks.DIRT_PATH);
        assertRing(helper, 1, Blocks.DIRT_PATH);
        assertRing(helper, 2, Blocks.DIRT_PATH);
        finish(helper, player);
    }

    @GameTest
    public void everySpecifiedSoilBecomesDirtPathAndNothingElseChanges(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        List<Block> soils = List.of(
                Blocks.GRASS_BLOCK,
                Blocks.DIRT,
                Blocks.COARSE_DIRT,
                Blocks.PODZOL,
                Blocks.MYCELIUM,
                Blocks.ROOTED_DIRT);
        for (Block soil : soils) {
            helper.setBlock(CENTER.below(), soil);

            trampleWithRadius(helper, player, 0);

            assertBlockIs(helper, CENTER.below(), Blocks.DIRT_PATH, soil + " must become dirt path");
        }

        helper.setBlock(CENTER.below(), Blocks.STONE);
        trampleWithRadius(helper, player, 0);
        assertBlockIs(helper, CENTER.below(), Blocks.STONE, "Soil outside the list must be kept");
        finish(helper, player);
    }

    @GameTest
    public void listedVegetationIsDestroyedAtFeetAndHead(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        List<Block> plants = List.of(
                Blocks.POPPY,
                Blocks.DANDELION,
                Blocks.SHORT_GRASS,
                Blocks.FERN,
                Blocks.DEAD_BUSH,
                Blocks.SWEET_BERRY_BUSH);
        for (Block plant : plants) {
            assertDestroyedAtFeet(helper, player, plant);
            assertDestroyedAtHead(helper, player, plant);
        }
        for (Block doublePlant : List.of(Blocks.TALL_GRASS, Blocks.LARGE_FERN)) {
            assertDoublePlantDestroyed(helper, player, doublePlant);
        }

        helper.setBlock(CENTER, Blocks.DIRT);
        helper.setBlock(CENTER.above(), Blocks.OAK_SAPLING);
        trampleWithRadius(helper, player, 0);
        assertBlockIs(
                helper,
                CENTER.above(),
                Blocks.OAK_SAPLING,
                "Vegetation outside the list must be kept");
        finish(helper, player);
    }

    @GameTest
    public void theEnchantmentDrivesTheEffectOnlyWhileTheFeatureIsEnabled(GameTestHelper helper) {
        ServerPlayer player = playerOnCenter(helper);
        player.setItemSlot(EquipmentSlot.FEET, enchantedBoots(helper));

        Heavyfoot.enable(1);
        helper.setBlock(CENTER.below(), Blocks.GRASS_BLOCK);
        EnchantmentHelper.tickEffects(helper.getLevel(), player);
        assertBlockIs(
                helper,
                CENTER.below(),
                Blocks.DIRT_PATH,
                "Enchanted boots must trample while the feature is enabled");

        Heavyfoot.disable();
        helper.setBlock(CENTER.below(), Blocks.GRASS_BLOCK);
        EnchantmentHelper.tickEffects(helper.getLevel(), player);
        assertBlockIs(
                helper,
                CENTER.below(),
                Blocks.GRASS_BLOCK,
                "A disabled feature must suspend the effect of the enchantment");

        Heavyfoot.enable(1);
        player.setItemSlot(EquipmentSlot.FEET, Items.LEATHER_BOOTS.getDefaultInstance());
        helper.setBlock(CENTER.below(), Blocks.GRASS_BLOCK);
        EnchantmentHelper.tickEffects(helper.getLevel(), player);
        assertBlockIs(
                helper,
                CENTER.below(),
                Blocks.GRASS_BLOCK,
                "Boots without the enchantment must not trample");
        finish(helper, player);
    }

    @GameTest
    public void playersForbiddenFromChangingBlocksDoNotTrample(GameTestHelper helper) {
        for (GameType gameMode : List.of(GameType.SPECTATOR, GameType.ADVENTURE)) {
            Player player = helper.makeMockPlayer(gameMode);
            gameMode.updatePlayerAbilities(player.getAbilities());
            BlockPos absolute = helper.absolutePos(CENTER);
            player.setPosRaw(absolute.getX() + 0.5, absolute.getY(), absolute.getZ() + 0.5);
            helper.setBlock(CENTER.below(), Blocks.GRASS_BLOCK);
            helper.setBlock(CENTER, Blocks.POPPY);

            Heavyfoot.enable(1);
            Heavyfoot.trample(helper.getLevel(), player);

            assertBlockIs(
                    helper,
                    CENTER.below(),
                    Blocks.GRASS_BLOCK,
                    "A player in " + gameMode + " must not flatten soil");
            assertBlockIs(
                    helper,
                    CENTER,
                    Blocks.POPPY,
                    "A player in " + gameMode + " must not destroy vegetation");
            player.discard();
        }
        helper.succeed();
    }

    private static void assertDestroyedAtFeet(
            GameTestHelper helper, ServerPlayer player, Block plant) {
        helper.setBlock(CENTER.below(), Blocks.DIRT);
        helper.setBlock(CENTER, plant);

        trampleWithRadius(helper, player, 0);

        assertBlockIs(helper, CENTER, Blocks.AIR, plant + " must be destroyed at the feet");
    }

    private static void assertDestroyedAtHead(
            GameTestHelper helper, ServerPlayer player, Block plant) {
        helper.setBlock(CENTER, Blocks.DIRT);
        helper.setBlock(CENTER.above(), plant);

        trampleWithRadius(helper, player, 0);

        assertBlockIs(helper, CENTER.above(), Blocks.AIR, plant + " must be destroyed at the head");
        helper.setBlock(CENTER, Blocks.AIR);
    }

    private static void assertDoublePlantDestroyed(
            GameTestHelper helper, ServerPlayer player, Block plant) {
        helper.setBlock(CENTER.below(), Blocks.DIRT);
        DoublePlantBlock.placeAt(
                helper.getLevel(), plant.defaultBlockState(), helper.absolutePos(CENTER), 3);

        trampleWithRadius(helper, player, 0);

        assertBlockIs(helper, CENTER, Blocks.AIR, plant + " must be destroyed at the feet");
        assertBlockIs(helper, CENTER.above(), Blocks.AIR, plant + " must not leave its upper half");
    }

    private static ItemStack enchantedBoots(GameTestHelper helper) {
        ItemStack boots = Items.LEATHER_BOOTS.getDefaultInstance();
        boots.enchant(
                helper.getLevel()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(RootBootEnchantments.HEAVYFOOT),
                1);
        return boots;
    }

    private static ServerPlayer playerOnCenter(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        BlockPos absolute = helper.absolutePos(CENTER);
        player.setPosRaw(absolute.getX() + 0.5, absolute.getY(), absolute.getZ() + 0.5);
        return player;
    }

    private static void trampleWithRadius(GameTestHelper helper, ServerPlayer player, int radius) {
        Heavyfoot.enable(radius);
        Heavyfoot.trample(helper.getLevel(), player);
    }

    private static void fillSoil(GameTestHelper helper, int rings) {
        for (int x = -rings; x <= rings; x++) {
            for (int z = -rings; z <= rings; z++) {
                helper.setBlock(CENTER.offset(x, -1, z), Blocks.GRASS_BLOCK);
            }
        }
    }

    /** Asserts the soil layer of the square ring at the given distance from the center. */
    private static void assertRing(GameTestHelper helper, int ring, Block expected) {
        for (int x = -ring; x <= ring; x++) {
            for (int z = -ring; z <= ring; z++) {
                if (Math.max(Math.abs(x), Math.abs(z)) != ring) {
                    continue;
                }
                BlockPos pos = CENTER.offset(x, -1, z);
                assertBlockIs(helper, pos, expected, "Ring " + ring + " at " + pos);
            }
        }
    }

    private static void assertBlockIs(
            GameTestHelper helper, BlockPos pos, Block expected, String message) {
        helper.assertBlock(
                pos,
                block -> block == expected,
                found -> Component.literal(
                        message + " (expected " + expected + ", found " + found + ")"));
    }

    private static void finish(GameTestHelper helper, ServerPlayer player) {
        helper.getLevel().getServer().getPlayerList().remove(player);
        helper.succeed();
    }
}
