/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1792$class_1793
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 */
package ru.realisticthunder;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2561;

public class StormCharm
extends class_1792 {
    public StormCharm(class_1792.class_1793 settings) {
        super(settings);
    }

    public class_1269 method_7836(class_1937 world, class_1657 player, class_1268 hand) {
        class_1799 stack = player.method_5998(hand);
        if (player.method_7357().method_7904(stack)) {
            return class_1269.field_5814;
        }
        if (!world.method_8608()) {
            player.method_6092(new class_1293(class_1294.field_5907, 400, 0));
            player.method_6092(new class_1293(class_1294.field_5918, 400, 0));
            player.method_7357().method_62835(stack, 1800);
            player.method_7353((class_2561)class_2561.method_43470((String)"\u0413\u0440\u043e\u0437\u043e\u0432\u043e\u0439 \u043e\u0431\u0435\u0440\u0435\u0433: \u0441\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435 \u0438 \u0437\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u043e\u0433\u043d\u044f \u043d\u0430 20 \u0441\u0435\u043a\u0443\u043d\u0434"), true);
        }
        return class_1269.field_5812;
    }
}

