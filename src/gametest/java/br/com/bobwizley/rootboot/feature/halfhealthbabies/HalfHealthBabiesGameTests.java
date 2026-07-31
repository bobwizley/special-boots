package br.com.bobwizley.rootboot.feature.halfhealthbabies;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class HalfHealthBabiesGameTests {

    private static final BlockPos SPAWN = new BlockPos(1, 2, 1);

    private static final Identifier FOREIGN_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("rootboot_test", "foreign_max_health");

    private static final float TOLERANCE = 1.0E-4F;

    @GameTest
    public void babiesOfDistinctTypesKeepHalfOfTheirMaxHealth(GameTestHelper helper) {
        HalfHealthBabies.enable();

        List<EntityType<? extends Mob>> types =
                List.of(EntityTypes.ZOMBIE, EntityTypes.COW, EntityTypes.HOGLIN);
        for (EntityType<? extends Mob> type : types) {
            Mob mob = helper.spawn(type, SPAWN);
            setBaby(mob, false);
            HalfHealthBabies.applyCurrentPolicy(mob);
            float adultMaxHealth = mob.getMaxHealth();

            setBaby(mob, true);
            HalfHealthBabies.applyCurrentPolicy(mob);

            assertClose(
                    helper,
                    mob.getMaxHealth(),
                    adultMaxHealth / 2.0F,
                    "A baby " + type.toShortString() + " must keep half of its max health");
            mob.discard();
        }
        helper.succeed();
    }

    @GameTest
    public void theTickHookAppliesTheReduction(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Cow cow = helper.spawn(EntityTypes.COW, SPAWN);
        cow.setNoAi(true);
        float adultMaxHealth = cow.getMaxHealth();
        cow.setBaby(true);

        cow.tick();

        assertClose(
                helper,
                cow.getMaxHealth(),
                adultMaxHealth / 2.0F,
                "Ticking a baby must apply the reduction");
        helper.succeed();
    }

    @GameTest
    public void modifiersFromOtherModsRemainInTheCalculation(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = baby(helper);
        AttributeInstance maxHealth = zombie.getAttribute(Attributes.MAX_HEALTH);
        float reducedMaxHealth = zombie.getMaxHealth();

        maxHealth.addPermanentModifier(new AttributeModifier(
                FOREIGN_MODIFIER_ID, 20.0, AttributeModifier.Operation.ADD_VALUE));

        assertClose(
                helper,
                zombie.getMaxHealth(),
                reducedMaxHealth + 10.0F,
                "A foreign modifier must be halved along with the rest of the value");
        helper.succeed();
    }

    @GameTest
    public void repeatedApplicationsDoNotStackTheReduction(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = baby(helper);
        AttributeInstance maxHealth = zombie.getAttribute(Attributes.MAX_HEALTH);

        maxHealth.setBaseValue(maxHealth.getBaseValue() * 2.0);
        for (int tick = 0; tick < 20; tick++) {
            HalfHealthBabies.applyCurrentPolicy(zombie);
        }

        assertClose(
                helper,
                zombie.getMaxHealth(),
                (float) maxHealth.getBaseValue() / 2.0F,
                "A later max health change must stay reduced by exactly one half");
        helper.assertValueEqual(
                maxHealth.getModifiers().size(), 1, "max health modifier count");
        helper.succeed();
    }

    @GameTest
    public void becomingABabyPreservesTheCurrentHealthRatio(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, SPAWN);
        zombie.setBaby(false);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        float adultMaxHealth = zombie.getMaxHealth();
        zombie.setHealth(adultMaxHealth * 0.6F);

        zombie.setBaby(true);
        HalfHealthBabies.applyCurrentPolicy(zombie);

        assertClose(helper, zombie.getMaxHealth(), adultMaxHealth / 2.0F, "baby max health");
        assertClose(helper, zombie.getHealth(), adultMaxHealth * 0.3F, "baby current health");
        helper.succeed();
    }

    @GameTest
    public void growingUpPreservesTheCurrentHealthRatio(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, SPAWN);
        zombie.setBaby(false);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        float adultMaxHealth = zombie.getMaxHealth();
        zombie.setBaby(true);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        zombie.setHealth(zombie.getMaxHealth() * 0.6F);

        zombie.setBaby(false);
        HalfHealthBabies.applyCurrentPolicy(zombie);

        assertClose(helper, zombie.getMaxHealth(), adultMaxHealth, "grown up max health");
        assertClose(helper, zombie.getHealth(), adultMaxHealth * 0.6F, "grown up current health");
        helper.succeed();
    }

    @GameTest
    public void reloadingABabyKeepsTheReductionAndItsHealth(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = baby(helper);
        float reducedMaxHealth = zombie.getMaxHealth();
        zombie.setHealth(reducedMaxHealth * 0.5F);

        LivingEntity loaded = reload(helper, zombie);
        HalfHealthBabies.applyCurrentPolicy(loaded);

        assertClose(helper, loaded.getMaxHealth(), reducedMaxHealth, "reloaded baby max health");
        assertClose(
                helper,
                loaded.getHealth(),
                reducedMaxHealth * 0.5F,
                "reloaded baby current health");
        helper.assertValueEqual(
                loaded.getAttribute(Attributes.MAX_HEALTH).getModifiers().size(),
                1,
                "max health modifier count");
        helper.succeed();
    }

    @GameTest
    public void loadingWithTheFeatureDisabledRestoresTheMaxHealth(GameTestHelper helper) {
        HalfHealthBabies.enable();
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, SPAWN);
        zombie.setBaby(false);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        float adultMaxHealth = zombie.getMaxHealth();
        zombie.setBaby(true);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        zombie.setHealth(zombie.getMaxHealth() * 0.3F);

        HalfHealthBabies.disable();
        LivingEntity loaded = reload(helper, zombie);
        HalfHealthBabies.applyCurrentPolicy(loaded);

        assertClose(helper, loaded.getMaxHealth(), adultMaxHealth, "reverted baby max health");
        assertClose(
                helper,
                loaded.getHealth(),
                adultMaxHealth * 0.3F,
                "reverted baby current health");
        HalfHealthBabies.enable();
        helper.succeed();
    }

    @GameTest
    public void aDisabledFeatureDoesNotReduceNewBabies(GameTestHelper helper) {
        HalfHealthBabies.disable();
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, SPAWN);
        zombie.setBaby(false);
        float adultMaxHealth = zombie.getMaxHealth();

        zombie.setBaby(true);
        HalfHealthBabies.applyCurrentPolicy(zombie);

        assertClose(
                helper,
                zombie.getMaxHealth(),
                adultMaxHealth,
                "A disabled feature must leave a new baby untouched");
        HalfHealthBabies.enable();
        helper.succeed();
    }

    private static Zombie baby(GameTestHelper helper) {
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, SPAWN);
        zombie.setBaby(true);
        HalfHealthBabies.applyCurrentPolicy(zombie);
        return zombie;
    }

    private static void setBaby(Mob mob, boolean baby) {
        if (mob instanceof Zombie zombie) {
            zombie.setBaby(baby);
        } else {
            ((AgeableMob) mob).setBaby(baby);
        }
    }

    /** Round-trips the entity through its save data, the way unloading and loading a chunk does. */
    private static LivingEntity reload(GameTestHelper helper, LivingEntity entity) {
        ServerLevel level = helper.getLevel();
        TagValueOutput saved =
                TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        entity.save(saved);
        entity.discard();

        Entity loaded = EntityType.loadEntityRecursive(
                TagValueInput.create(
                        ProblemReporter.DISCARDING, level.registryAccess(), saved.buildResult()),
                level,
                EntitySpawnReason.LOAD,
                EntityProcessor.NOP);
        if (!(loaded instanceof LivingEntity livingEntity)) {
            helper.fail(Component.literal("The entity could not be reloaded from its save data"));
            throw new IllegalStateException();
        }
        return livingEntity;
    }

    private static void assertClose(
            GameTestHelper helper, float actual, float expected, String description) {
        if (Math.abs(actual - expected) > TOLERANCE) {
            helper.fail(Component.literal(
                    description + " (expected " + expected + ", found " + actual + ")"));
        }
    }
}
