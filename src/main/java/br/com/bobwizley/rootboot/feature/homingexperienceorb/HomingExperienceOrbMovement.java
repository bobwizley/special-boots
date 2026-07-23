package br.com.bobwizley.rootboot.feature.homingexperienceorb;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class HomingExperienceOrbMovement {

    public static final int DELAY_TICKS = 20;
    public static final int SLOW_TICKS = 10;
    public static final double SLOW_SPEED = 0.3;
    public static final double FAST_SPEED = 0.6;
    public static final double RANGE = 64.0;

    private static boolean enabled;

    private HomingExperienceOrbMovement() {
    }

    static void enable() {
        enabled = true;
    }

    static void disable() {
        enabled = false;
    }

    public static boolean isEnabled(ExperienceOrb orb) {
        return enabled && !orb.level().isClientSide() && orb.tickCount > DELAY_TICKS;
    }

    public static Player nearestTarget(ExperienceOrb orb) {
        double rangeSquared = RANGE * RANGE;
        Player nearest = null;
        double nearestDistanceSquared = rangeSquared;
        for (Player player : orb.level().getServer().getPlayerList().getPlayers()) {
            double distanceSquared = player.distanceToSqr(orb);
            if (player.level() == orb.level()
                    && !player.isSpectator()
                    && distanceSquared <= nearestDistanceSquared) {
                nearest = player;
                nearestDistanceSquared = distanceSquared;
            }
        }
        return nearest;
    }

    public static Vec3 movement(ExperienceOrb orb, Player target, int pursuitTicks) {
        if (target == null) {
            return Vec3.ZERO;
        }

        Vec3 direction = target.position().subtract(orb.position());
        if (direction.lengthSqr() == 0.0) {
            return Vec3.ZERO;
        }

        double speed = pursuitTicks < SLOW_TICKS ? SLOW_SPEED : FAST_SPEED;
        return direction.normalize().scale(speed);
    }
}
