package br.com.bobwizley.rootboot.feature.deathitemprotection;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DeathItemProtectionGameTests {

    @GameTest
    public void deathDropsReceiveUnlimitedLifetime(GameTestHelper helper) {
        DeathItemProtection.enable();
        ServerPlayer player = playerWithItem(helper);

        player.die(helper.getLevel().damageSources().genericKill());

        ItemEntity droppedItem = onlyDroppedItem(helper);
        removePlayer(helper, player);
        helper.assertTrue(
                ((ProtectedDeathItem) droppedItem).rootboot$isProtectedDeathItem(),
                "An item dropped by a player death must be protected");
        tick(droppedItem, 6_001);
        helper.assertFalse(
                droppedItem.isRemoved(),
                "A protected death item must not despawn after the vanilla lifetime");
        helper.succeed();
    }

    @GameTest
    public void protectedDeathDropsRemainVulnerableToDamage(GameTestHelper helper) {
        DeathItemProtection.enable();
        ServerPlayer player = playerWithItem(helper);
        player.die(helper.getLevel().damageSources().genericKill());
        onlyDroppedItem(helper);
        removePlayer(helper, player);

        List<DamageSource> hazards = List.of(
                helper.getLevel().damageSources().inFire(),
                helper.getLevel().damageSources().lava(),
                helper.getLevel().damageSources().cactus(),
                helper.getLevel().damageSources().fellOutOfWorld(),
                helper.getLevel().damageSources().explosion(null));
        for (DamageSource hazard : hazards) {
            ItemEntity item = itemAt(helper, Items.DIAMOND.getDefaultInstance());
            DeathItemProtection.protect(item);
            item.hurtServer(helper.getLevel(), hazard, 5.0F);
            helper.assertTrue(
                    item.isRemoved(),
                    "Protection from time must not block " + hazard.typeHolder());
        }
        helper.succeed();
    }

    @GameTest
    public void disablingProtectionStartsAFreshVanillaLifetime(GameTestHelper helper) {
        DeathItemProtection.enable();
        ItemEntity savedItem = itemAt(helper, Items.DIAMOND.getDefaultInstance());
        DeathItemProtection.protect(savedItem);
        tick(savedItem, 6_001);
        ItemEntity item = itemAt(helper, Items.DIAMOND.getDefaultInstance());
        item.restoreFrom(savedItem);
        savedItem.discard();

        DeathItemProtection.disable();
        item.tick();

        helper.assertFalse(
                ((ProtectedDeathItem) item).rootboot$isProtectedDeathItem(),
                "Disabling the feature must remove the persistent protection mark");
        tick(item, 5_998);
        helper.assertFalse(
                item.isRemoved(),
                "A formerly protected item must receive a complete new vanilla lifetime");
        item.tick();
        helper.assertTrue(
                item.isRemoved(),
                "A formerly protected item must despawn after its new vanilla lifetime");
        helper.succeed();
    }

    @GameTest
    public void deathWithoutVanillaDropsDoesNotCreateProtectedItems(GameTestHelper helper) {
        DeathItemProtection.enable();
        ServerPlayer player = playerWithItem(helper);
        player.getInventory().clearContent();

        player.die(helper.getLevel().damageSources().genericKill());
        removePlayer(helper, player);

        helper.assertTrue(
                droppedItems(helper).isEmpty(),
                "A death without vanilla item drops must not create protected items");
        helper.succeed();
    }

    private static ServerPlayer playerWithItem(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        Vec3 position = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        player.setPosRaw(position.x, position.y, position.z);
        player.getInventory().add(Items.DIAMOND.getDefaultInstance());
        return player;
    }

    private static ItemEntity itemAt(GameTestHelper helper, ItemStack stack) {
        Vec3 position = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        ItemEntity item =
                new ItemEntity(helper.getLevel(), position.x, position.y, position.z, stack);
        helper.getLevel().addFreshEntity(item);
        return item;
    }

    private static ItemEntity onlyDroppedItem(GameTestHelper helper) {
        List<ItemEntity> items = droppedItems(helper);
        helper.assertValueEqual(items.size(), 1, "death drop count");
        return items.getFirst();
    }

    private static List<ItemEntity> droppedItems(GameTestHelper helper) {
        Vec3 center = helper.absoluteVec(new Vec3(1.0, 4.0, 1.0));
        return helper.getLevel().getEntitiesOfClass(
                ItemEntity.class, AABB.ofSize(center, 8.0, 8.0, 8.0));
    }

    private static void removePlayer(GameTestHelper helper, ServerPlayer player) {
        helper.getLevel().getServer().getPlayerList().remove(player);
    }

    private static void tick(ItemEntity item, int count) {
        for (int tick = 0; tick < count; tick++) {
            item.tick();
        }
    }
}
