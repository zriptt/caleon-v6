package com.caleon.client.mixin;

import com.caleon.client.module.AntiTrap;
import com.caleon.client.module.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void caleon$look(double dx, double dy, CallbackInfo ci) {
        if (Freecam.active && (Object) this == MinecraftClient.getInstance().player) {
            Freecam.look(dx, dy);
            ci.cancel();
        }
    }

    // ---- Anti Trap: armor stands / minecarts become invisible, non-solid and un-targetable ----

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true, require = 0)
    private void caleon$antiTrapInvisible(CallbackInfoReturnable<Boolean> cir) {
        if (AntiTrap.hides((Entity) (Object) this)) cir.setReturnValue(true);
    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true, require = 0)
    private void caleon$antiTrapCollide(CallbackInfoReturnable<Boolean> cir) {
        if (AntiTrap.hides((Entity) (Object) this)) cir.setReturnValue(false);
    }

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true, require = 0)
    private void caleon$antiTrapBox(CallbackInfoReturnable<Box> cir) {
        Entity self = (Entity) (Object) this;
        if (AntiTrap.hides(self)) {
            // zero-volume box at the entity's own position: it still "exists" there, but nothing
            // can collide with or raycast-select it, so you walk/attack straight through it and
            // it never becomes your crosshair target (which is what stops you opening it).
            var pos = self.getPos();
            cir.setReturnValue(new Box(pos, pos));
        }
    }
}
