package br.com.bobwizley.rootboot.feature.homingexperienceorb;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class HomingExperienceOrbGameTests {

    @GameTest(maxTicks = 40)
    public void coversHomingExperienceOrbBehavior(GameTestHelper helper) {
        ServerPlayer player = playerAt(helper, helper.absoluteVec(new Vec3(1.0, 4.0, 1.0)), false);
        waitsTwentyTicksBeforePursuing(helper, player);
        recalculatesTargetAndHonorsRange(helper, player);
        usesSpecifiedSpeedsAndMovesThroughBlocks(helper, player);
        leavesVanillaMovementUntouchedWhenDisabled(helper, player);
        removePlayer(helper, player);
        helper.succeed();
    }

    private static void waitsTwentyTicksBeforePursuing(
            GameTestHelper helper, ServerPlayer player) {
        Vec3 start = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        player.setGameMode(GameType.SURVIVAL);
        player.setPosRaw(start.x + 10.0, start.y, start.z);
        assertClose(helper, start.x + 10.0, player.getX(), "The test player must be positioned");
        helper.assertTrue(
                player.level() == helper.getLevel(), "The test player must share the orb level");
        helper.assertTrue(
                helper.getLevel().getServer().getPlayerList().getPlayers().contains(player),
                "The test player must be in the server player list");
        helper.assertFalse(player.isSpectator(), "The test player must be eligible");
        ExperienceOrb orb = orbAt(helper, start);

        for (int tick = 0; tick < 20; tick++) {
            tickFrom(orb, start);
        }

        helper.assertTrue(
                orb.getX() < start.x + 0.1,
                "The feature must not pursue during the first 20 ticks");

        tickFrom(orb, start);

        assertClose(
                helper,
                start.x + 0.3,
                orb.getX(),
                "The orb must start pursuing on tick 21");
    }

    private static void recalculatesTargetAndHonorsRange(
            GameTestHelper helper, ServerPlayer player) {
        Vec3 start = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        player.setGameMode(GameType.SURVIVAL);
        player.setPosRaw(start.x + 10.0, start.y, start.z);
        ExperienceOrb orb = homingOrbAt(helper, start);

        tickFrom(orb, start);
        helper.assertTrue(orb.getX() > start.x, "The orb must pursue an eligible nearby player");

        player.setPos(start.add(-10.0, 0.0, 0.0));
        tickFrom(orb, start);
        helper.assertTrue(orb.getX() < start.x, "The target must be recalculated after movement");

        player.setPos(start.add(65.0, 0.0, 0.0));
        tickFrom(orb, start);
        assertClose(helper, start.x, orb.getX(), "A player beyond 64 blocks must be ignored");

    }

    private static void usesSpecifiedSpeedsAndMovesThroughBlocks(
            GameTestHelper helper, ServerPlayer player) {
        Vec3 start = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        player.setGameMode(GameType.SURVIVAL);
        player.setPosRaw(start.x + 10.0, start.y, start.z);
        ExperienceOrb orb = homingOrbAt(helper, start);

        helper.setBlock(2, 4, 1, Blocks.STONE);
        for (int pursuitTick = 0; pursuitTick < 10; pursuitTick++) {
            tickFrom(orb, start);
            assertClose(
                    helper,
                    start.x + 0.3,
                    orb.getX(),
                    "The first ten pursuit ticks must move 0.3 blocks");
        }

        tickFrom(orb, start);
        assertClose(
                helper,
                start.x + 0.6,
                orb.getX(),
                "Pursuit after ten effective ticks must move 0.6 blocks");

        orb.setPos(start);
        for (int tick = 0; tick < 4; tick++) {
            orb.tick();
        }
        helper.assertTrue(
                orb.getX() > helper.absoluteVec(new Vec3(2.0, 4.0, 1.0)).x,
                "The homing movement must cross intervening blocks");
    }

    private static void leavesVanillaMovementUntouchedWhenDisabled(
            GameTestHelper helper, ServerPlayer player) {
        Vec3 start = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        player.setGameMode(GameType.SURVIVAL);
        player.setPosRaw(start.x + 10.0, start.y, start.z);
        ExperienceOrb orb = homingOrbAt(helper, start);

        HomingExperienceOrbMovement.disable();
        try {
            tickFrom(orb, start);
            assertClose(
                    helper,
                    start.x,
                    orb.getX(),
                    "A disabled feature must preserve vanilla horizontal movement");
            helper.assertTrue(
                    orb.getY() < start.y,
                    "A disabled feature must preserve vanilla gravity");
        } finally {
            HomingExperienceOrbMovement.enable();
        }
    }

    private static ServerPlayer playerAt(
            GameTestHelper helper, Vec3 position, boolean spectator) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(spectator ? GameType.SPECTATOR : GameType.SURVIVAL);
        player.setPosRaw(position.x, position.y, position.z);
        return player;
    }

    private static void removePlayer(GameTestHelper helper, ServerPlayer player) {
        helper.getLevel().getServer().getPlayerList().remove(player);
    }

    private static ExperienceOrb orbAt(GameTestHelper helper, Vec3 position) {
        ExperienceOrb orb =
                new ExperienceOrb(helper.getLevel(), position.x, position.y, position.z, 1);
        orb.setDeltaMovement(Vec3.ZERO);
        return orb;
    }

    private static ExperienceOrb homingOrbAt(GameTestHelper helper, Vec3 position) {
        ExperienceOrb orb = orbAt(helper, position);
        for (int tick = 0; tick < 20; tick++) {
            tickFrom(orb, position);
        }
        return orb;
    }

    private static void tickFrom(ExperienceOrb orb, Vec3 position) {
        orb.setPos(position);
        orb.setDeltaMovement(Vec3.ZERO);
        orb.tickCount++;
        orb.tick();
    }

    private static void assertClose(
            GameTestHelper helper, double expected, double actual, String message) {
        helper.assertTrue(
                Math.abs(expected - actual) < 0.0001,
                message + " (expected " + expected + ", got " + actual + ")");
    }
}
