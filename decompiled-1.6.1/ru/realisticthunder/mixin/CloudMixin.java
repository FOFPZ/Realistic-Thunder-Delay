/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_9955
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package ru.realisticthunder.mixin;

import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_9955;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={class_9955.class})
public class CloudMixin {
    @ModifyVariable(method={"method_62168"}, at=@At(value="HEAD"), argsOnly=true, ordinal=0)
    private int weatherCloudColor(int color) {
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return color;
        }
        float factor = 1.0f - 0.2f * world.method_8430(1.0f) - 0.22f * world.method_8478(1.0f);
        int a = color >>> 24 & 0xFF;
        int r = (int)((float)(color >>> 16 & 0xFF) * factor);
        int g = (int)((float)(color >>> 8 & 0xFF) * factor);
        int b = (int)((float)(color & 0xFF) * factor);
        return a << 24 | r << 16 | g << 8 | b;
    }
}

