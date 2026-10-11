package com.caleon.client.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

/**
 * Makes armor stands and minecarts (including chest minecarts) invisible, non-solid and
 * un-interactable on your own client - they can't block your movement or your attacks, and you
 * can't accidentally open or trigger them. Entirely client-side; other players still see them normally.
 */
public class AntiTrap extends Module {
    public static boolean active;

    public AntiTrap() { super("Anti Trap", Category.MISC); }

    /** Used by EntityMixin to decide which entities to hide. */
    public static boolean hides(Entity e) {
        return active && (e instanceof ArmorStandEntity || e instanceof AbstractMinecartEntity);
    }

    @Override public void onEnable() { active = true; reload(); }
    @Override public void onDisable() { active = false; reload(); }

    private void reload() {
        if (mc.world != null && mc.worldRenderer != null) mc.worldRenderer.reload();
    }
}
