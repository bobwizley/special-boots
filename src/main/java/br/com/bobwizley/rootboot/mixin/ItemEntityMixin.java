package br.com.bobwizley.rootboot.mixin;

import br.com.bobwizley.rootboot.feature.deathitemprotection.DeathItemProtection;
import br.com.bobwizley.rootboot.feature.deathitemprotection.ProtectedDeathItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin implements ProtectedDeathItem {

    @Shadow
    private int age;

    @Unique
    private static final String ROOTBOOT_PROTECTED_DEATH_ITEM = "rootboot_protected_death_item";

    @Unique
    private boolean rootboot$protectedDeathItem;

    @Override
    public boolean rootboot$isProtectedDeathItem() {
        return rootboot$protectedDeathItem;
    }

    @Override
    public void rootboot$setProtectedDeathItem(boolean protectedDeathItem) {
        rootboot$protectedDeathItem = protectedDeathItem;
    }

    @Override
    public void rootboot$resetDespawnAge() {
        age = 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void rootboot$updateDeathItemProtection(CallbackInfo ci) {
        DeathItemProtection.applyCurrentPolicy((ItemEntity) (Object) this);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void rootboot$saveDeathItemProtection(ValueOutput output, CallbackInfo ci) {
        if (rootboot$protectedDeathItem) {
            output.putBoolean(ROOTBOOT_PROTECTED_DEATH_ITEM, true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void rootboot$loadDeathItemProtection(ValueInput input, CallbackInfo ci) {
        rootboot$protectedDeathItem =
                input.getBooleanOr(ROOTBOOT_PROTECTED_DEATH_ITEM, false);
    }
}
