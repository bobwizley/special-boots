package br.com.bobwizley.rootboot.client.feature.localdeathsound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public final class LocalDeathSound {

    private static final float VOLUME = 1.0F;
    private static final float PITCH = 1.0F;

    /**
     * The fatal damage event and the death event are broadcast in the same server tick, but the
     * client may process them on either side of a level tick, so one tick of slack is the widest
     * window that still cannot pick up an earlier hit.
     */
    private static final long CAUSE_FRESHNESS_TICKS = 1L;

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

        long elapsed = entity.level().getGameTime() - lastCauseTime;
        DeathSoundCause cause = elapsed >= 0L && elapsed <= CAUSE_FRESHNESS_TICKS
                ? DeathSoundCause.of(lastCause)
                : DeathSoundCause.GENERIC;
        lastCause = null;
        Minecraft.getInstance().getSoundManager().play(localSound(soundEvent(cause)));
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

    /**
     * Attached to the listener rather than to a position, so nobody else can hear it, and filed
     * under the players category so it follows the slider the death it reports belongs to.
     */
    private static SoundInstance localSound(SoundEvent sound) {
        return new SimpleSoundInstance(sound.location(), SoundSource.PLAYERS, VOLUME, PITCH,
                SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0, true);
    }

    private static boolean isLocalPlayer(LivingEntity entity) {
        return entity == Minecraft.getInstance().player;
    }
}
