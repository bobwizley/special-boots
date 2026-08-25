package br.com.bobwizley.rootboot.client.feature.localdeathsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public final class LocalDeathSound {

    private static final float VOLUME = 1.0F;

    /**
     * The fatal damage event and the death event are broadcast in the same server tick, so a
     * cause older than that belongs to an earlier hit and must not be attributed to the death.
     */
    private static final long CAUSE_FRESHNESS_TICKS = 5L;

    private static boolean enabled;
    private static ResourceKey<DamageType> lastCause;
    private static long lastCauseTime;

    private LocalDeathSound() {
    }

    static void enable() {
        enabled = true;
    }

    public static void damageTaken(LivingEntity entity, DamageSource source) {
        if (!enabled || !isLocalPlayer(entity)) {
            return;
        }
        lastCause = source.typeHolder().unwrapKey().orElse(null);
        lastCauseTime = entity.level().getGameTime();
    }

    public static void died(LivingEntity entity) {
        if (!enabled || !isLocalPlayer(entity)) {
            return;
        }

        long now = entity.level().getGameTime();
        DeathSoundCause cause = now - lastCauseTime <= CAUSE_FRESHNESS_TICKS
                ? DeathSoundCause.of(lastCause)
                : DeathSoundCause.GENERIC;
        lastCause = null;
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(soundEvent(cause), VOLUME));
    }

    static SoundEvent soundEvent(DeathSoundCause cause) {
        return switch (cause) {
            case SLAIN -> SoundEvents.PLAYER_ATTACK_CRIT;
            case BURNED -> SoundEvents.FIRE_EXTINGUISH;
            case DROWNED -> SoundEvents.PLAYER_HURT_DROWN;
            case FELL -> SoundEvents.PLAYER_BIG_FALL;
            case CRUSHED -> SoundEvents.ANVIL_LAND;
            case BLOWN_UP -> SoundEvents.GENERIC_EXPLODE.value();
            case FROZEN -> SoundEvents.PLAYER_HURT_FREEZE;
            case WITHERED -> SoundEvents.WITHER_HURT;
            case GENERIC -> SoundEvents.GENERIC_DEATH;
        };
    }

    private static boolean isLocalPlayer(LivingEntity entity) {
        return entity == Minecraft.getInstance().player;
    }
}
