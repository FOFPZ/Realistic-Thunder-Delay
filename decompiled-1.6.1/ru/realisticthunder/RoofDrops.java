/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3419
 *  net.minecraft.class_638
 */
package ru.realisticthunder;

import java.util.Random;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3419;
import net.minecraft.class_638;

public final class RoofDrops {
    private static final Random random = new Random();
    private static int delay = 20;

    public static boolean hasRoof(class_638 world, class_2338 pos) {
        for (int i = 2; i <= 32; ++i) {
            class_2338 p = pos.method_10086(i);
            class_2680 state = world.method_8320(p);
            if (state.method_26215() || state.method_45474() || !state.method_26227().method_15769()) continue;
            return true;
        }
        return false;
    }

    public static void tick(class_310 client) {
        if (client.field_1687 == null || client.field_1724 == null || !client.field_1687.method_8419()) {
            delay = 20;
            return;
        }
        class_2338 pos = client.field_1724.method_24515();
        if (client.field_1687.method_70745(pos) != class_1959.class_1963.field_9382 || !RoofDrops.hasRoof(client.field_1687, pos)) {
            delay = 20;
            return;
        }
        if (--delay <= 0) {
            delay = 55 + random.nextInt(50);
            client.method_1483().method_4873((class_1113)new class_1109(class_2960.method_60655((String)"realistic_thunder", (String)"roof_drop"), class_3419.field_15252, 0.62f, 0.96f + random.nextFloat() * 0.08f, class_1113.method_43221(), false, 0, class_1113.class_1114.field_5478, 0.0, 0.0, 0.0, true));
        }
    }
}

