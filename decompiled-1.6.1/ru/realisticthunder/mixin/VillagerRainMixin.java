/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1646
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_3218
 *  net.minecraft.class_4095
 *  net.minecraft.class_4140
 *  net.minecraft.class_4142
 *  net.minecraft.class_4153$class_4155
 *  net.minecraft.class_4168
 *  net.minecraft.class_4208
 *  net.minecraft.class_7477
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.realisticthunder.mixin;

import java.util.Optional;
import net.minecraft.class_1646;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_4095;
import net.minecraft.class_4140;
import net.minecraft.class_4142;
import net.minecraft.class_4153;
import net.minecraft.class_4168;
import net.minecraft.class_4208;
import net.minecraft.class_7477;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_1646.class})
public class VillagerRainMixin {
    @Inject(method={"method_5958"}, at={@At(value="TAIL")})
    private void shelterInRain(class_3218 world, CallbackInfo ci) {
        class_1646 v = (class_1646)this;
        class_4095 brain = v.method_18868();
        if (!world.method_8419() || v.method_5987() || v.method_18009() || world.method_70745(v.method_24515()) != class_1959.class_1963.field_9382 || brain.method_18906(class_4168.field_18599) || brain.method_18906(class_4168.field_19041) || brain.method_18906(class_4168.field_19043) || brain.method_18906(class_4168.field_19042)) {
            return;
        }
        Optional home = brain.method_46873(class_4140.field_18438);
        if (home.isPresent() && ((class_4208)home.get()).comp_2207().equals(world.method_27983())) {
            if (!brain.method_18906(class_4168.field_18597)) {
                brain.method_24526(class_4168.field_18597);
            }
            if (v.field_6012 % 20 == 0 && v.method_5707(((class_4208)home.get()).comp_2208().method_46558()) > 6.25) {
                class_2338 p2 = ((class_4208)home.get()).comp_2208();
                brain.method_18878(class_4140.field_18445, (Object)new class_4142(p2, 1.2f, 1));
                v.method_5942().method_6337((double)p2.method_10263() + 0.5, (double)p2.method_10264(), (double)p2.method_10260() + 0.5, 1.2);
            }
        } else if (v.field_6012 % 40 == 0) {
            world.method_19494().method_20006(e -> e.method_40225(class_7477.field_39291), v.method_24515(), 48, class_4153.class_4155.field_18489).ifPresent(p -> {
                if (v.method_5707(p.method_46558()) > 6.25) {
                    brain.method_18878(class_4140.field_18445, (Object)new class_4142(p, 1.2f, 1));
                    v.method_5942().method_6337((double)p.method_10263() + 0.5, (double)p.method_10264(), (double)p.method_10260() + 0.5, 1.2);
                }
            });
        }
    }
}

