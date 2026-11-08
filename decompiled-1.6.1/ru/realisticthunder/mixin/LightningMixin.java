/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1538
 *  net.minecraft.class_1937
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.realisticthunder.mixin;

import net.minecraft.class_1538;
import net.minecraft.class_1937;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.realisticthunder.ThunderClient;

@Mixin(value={class_1538.class})
public class LightningMixin {
    @Redirect(method={"method_5773"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_1937;method_8486(DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FFZ)V"))
    private void realisticThunder(class_1937 world, double x, double y, double z, class_3414 sound, class_3419 category, float volume, float pitch, boolean useDistance) {
        if (sound == class_3417.field_14865) {
            ThunderClient.schedule(world, x, y, z);
        } else if (sound != class_3417.field_14956) {
            world.method_8486(x, y, z, sound, category, volume, pitch, useDistance);
        }
    }
}

