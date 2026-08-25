package br.com.bobwizley.rootboot.client.feature.localdeathsound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

/**
 * The groups of damage types the local death sound distinguishes. Anything outside the table,
 * including damage types added by other mods and deaths whose cause never reached the client,
 * falls back to {@link #GENERIC}.
 */
public enum DeathSoundCause {

    SLAIN,
    BURNED,
    DROWNED,
    FELL,
    CRUSHED,
    BLOWN_UP,
    FROZEN,
    WITHERED,
    GENERIC;

    private static final Map<ResourceKey<DamageType>, DeathSoundCause> BY_DAMAGE_TYPE =
            byDamageType();

    public static DeathSoundCause of(ResourceKey<DamageType> damageType) {
        return damageType == null ? GENERIC : BY_DAMAGE_TYPE.getOrDefault(damageType, GENERIC);
    }

    private static Map<ResourceKey<DamageType>, DeathSoundCause> byDamageType() {
        Map<ResourceKey<DamageType>, DeathSoundCause> causes = new HashMap<>();
        put(causes, SLAIN, DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK, DamageTypes.ARROW, DamageTypes.TRIDENT,
                DamageTypes.SPEAR, DamageTypes.MOB_PROJECTILE, DamageTypes.THROWN,
                DamageTypes.SPIT, DamageTypes.STING, DamageTypes.THORNS, DamageTypes.MACE_SMASH);
        put(causes, BURNED, DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.HOT_FLOOR, DamageTypes.CAMPFIRE, DamageTypes.SULFUR_CUBE_HOT,
                DamageTypes.FIREBALL, DamageTypes.UNATTRIBUTED_FIREBALL,
                DamageTypes.DRAGON_BREATH);
        put(causes, DROWNED, DamageTypes.DROWN);
        put(causes, FELL, DamageTypes.FALL, DamageTypes.STALAGMITE, DamageTypes.FLY_INTO_WALL,
                DamageTypes.ENDER_PEARL);
        put(causes, CRUSHED, DamageTypes.FALLING_BLOCK, DamageTypes.FALLING_ANVIL,
                DamageTypes.FALLING_STALACTITE, DamageTypes.IN_WALL, DamageTypes.CRAMMING);
        put(causes, BLOWN_UP, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION,
                DamageTypes.WIND_CHARGE, DamageTypes.FIREWORKS, DamageTypes.BAD_RESPAWN_POINT);
        put(causes, FROZEN, DamageTypes.FREEZE);
        put(causes, WITHERED, DamageTypes.WITHER, DamageTypes.WITHER_SKULL, DamageTypes.MAGIC,
                DamageTypes.INDIRECT_MAGIC, DamageTypes.SONIC_BOOM);
        return Map.copyOf(causes);
    }

    @SafeVarargs
    private static void put(Map<ResourceKey<DamageType>, DeathSoundCause> causes,
            DeathSoundCause cause, ResourceKey<DamageType>... damageTypes) {
        for (ResourceKey<DamageType> damageType : List.of(damageTypes)) {
            causes.put(damageType, cause);
        }
    }
}
