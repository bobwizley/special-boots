package br.com.bobwizley.rootboot.client.feature.localdeathsound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageTypes;
import org.junit.jupiter.api.Test;

class DeathSoundCauseTest {

    @Test
    void mapsEachKnownDamageTypeToItsGroup() {
        assertEquals(DeathSoundCause.SLAIN, DeathSoundCause.of(DamageTypes.MOB_ATTACK));
        assertEquals(DeathSoundCause.BURNED, DeathSoundCause.of(DamageTypes.LAVA));
        assertEquals(DeathSoundCause.DROWNED, DeathSoundCause.of(DamageTypes.DROWN));
        assertEquals(DeathSoundCause.FELL, DeathSoundCause.of(DamageTypes.FALL));
        assertEquals(DeathSoundCause.CRUSHED, DeathSoundCause.of(DamageTypes.FALLING_ANVIL));
        assertEquals(DeathSoundCause.BLOWN_UP, DeathSoundCause.of(DamageTypes.EXPLOSION));
        assertEquals(DeathSoundCause.FROZEN, DeathSoundCause.of(DamageTypes.FREEZE));
        assertEquals(DeathSoundCause.WITHERED, DeathSoundCause.of(DamageTypes.WITHER));
    }

    @Test
    void fallsBackToGenericForUnmappedAndModdedDamageTypes() {
        ResourceKey<net.minecraft.world.damagesource.DamageType> modded = ResourceKey.create(
                Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("othermod", "laser"));

        assertEquals(DeathSoundCause.GENERIC, DeathSoundCause.of(modded));
        assertEquals(DeathSoundCause.GENERIC, DeathSoundCause.of(DamageTypes.GENERIC_KILL));
    }

    @Test
    void fallsBackToGenericWhenNoCauseReachedTheClient() {
        assertEquals(DeathSoundCause.GENERIC, DeathSoundCause.of(null));
    }
}
