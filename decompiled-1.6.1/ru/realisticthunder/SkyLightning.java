/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1299
 *  net.minecraft.class_1538
 *  net.minecraft.class_1937
 *  net.minecraft.class_638
 */
package ru.realisticthunder;

import net.minecraft.class_1299;
import net.minecraft.class_1538;
import net.minecraft.class_1937;
import net.minecraft.class_638;

public class SkyLightning
extends class_1538 {
    public static final long MARK = 6491869649456594944L;
    public static final long MASK = -4294967296L;
    private int age;

    public SkyLightning(class_638 world, int id, double x, double y, double z, int variation) {
        super(class_1299.field_6112, (class_1937)world);
        this.method_5838(id);
        this.method_5814(x, y, z);
        this.field_7186 = 0x5A17C10D00000000L | (long)variation & 0xFFFFFFFFL;
    }

    public boolean method_5640(double squaredDistance) {
        return squaredDistance < 262144.0;
    }

    public void method_5773() {
        if (++this.age > 4) {
            this.method_31472();
        }
    }
}

