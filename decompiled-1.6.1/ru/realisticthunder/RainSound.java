/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1101
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3419
 *  net.minecraft.class_638
 */
package ru.realisticthunder;

import net.minecraft.class_1101;
import net.minecraft.class_1113;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2902;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_638;
import ru.realisticthunder.RoofDrops;

public class RainSound
extends class_1101 {
    private final class_638 world;
    private final int layer;

    public RainSound(class_638 world, int layer) {
        super(class_3414.method_47908((class_2960)class_2960.method_60655((String)"realistic_thunder", (String)(new String[]{"rain", "shelter", "heavy_rain"})[layer])), class_3419.field_15252, class_1113.method_43221());
        this.world = world;
        this.layer = layer;
        this.field_5446 = true;
        this.field_5451 = 0;
        this.field_18936 = true;
        this.field_5440 = class_1113.class_1114.field_5478;
        this.field_5442 = 0.01f;
    }

    public void method_16896() {
        class_310 client = class_310.method_1551();
        if (client.field_1687 != this.world || client.field_1724 == null || !this.world.method_8419()) {
            this.method_24876();
            return;
        }
        class_2338 pos = client.field_1724.method_24515();
        boolean rainBiome = this.world.method_70745(pos) == class_1959.class_1963.field_9382;
        int depth = Math.max(0, this.world.method_8598(class_2902.class_2903.field_13197, pos).method_10264() - pos.method_10264());
        float nearRoof = RoofDrops.hasRoof(this.world, pos) ? 1.0f : 0.0f;
        float deep = depth > 24 ? 0.3f : 1.0f;
        float rain = rainBiome ? this.world.method_8430(1.0f) : 0.0f;
        float storm = this.world.method_8478(1.0f);
        float target = switch (this.layer) {
            case 0 -> 0.4f * rain * deep * (nearRoof > 0.0f ? 0.3f : 1.0f);
            case 1 -> 0.62f * rain * nearRoof;
            default -> 0.18f * rain * storm * deep * (nearRoof > 0.0f ? 0.5f : 1.0f);
        };
        this.field_5442 += (target - this.field_5442) * 0.08f;
    }
}

