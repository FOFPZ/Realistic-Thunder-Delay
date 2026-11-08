/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2338
 *  net.minecraft.class_3414
 *  net.minecraft.class_3419
 *  net.minecraft.class_638
 *  net.minecraft.class_9976
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.realisticthunder.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_638;
import net.minecraft.class_9976;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={class_9976.class})
public class WeatherMixin {
    @Redirect(method={"method_62319"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_638;method_45446(Lnet/minecraft/class_2338;Lnet/minecraft/class_3414;Lnet/minecraft/class_3419;FFZ)V"))
    private void suppressRain(class_638 world, class_2338 pos, class_3414 event, class_3419 category, float volume, float pitch, boolean distance) {
    }
}

