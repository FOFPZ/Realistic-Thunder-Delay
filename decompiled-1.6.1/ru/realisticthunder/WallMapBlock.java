/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.class_1750
 *  net.minecraft.class_1922
 *  net.minecraft.class_2237
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2464
 *  net.minecraft.class_2586
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2689$class_2690
 *  net.minecraft.class_2741
 *  net.minecraft.class_2754
 *  net.minecraft.class_2769
 *  net.minecraft.class_3726
 *  net.minecraft.class_4970$class_2251
 */
package ru.realisticthunder;

import com.mojang.serialization.MapCodec;
import net.minecraft.class_1750;
import net.minecraft.class_1922;
import net.minecraft.class_2237;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2464;
import net.minecraft.class_2586;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2741;
import net.minecraft.class_2754;
import net.minecraft.class_2769;
import net.minecraft.class_3726;
import net.minecraft.class_4970;
import ru.realisticthunder.WallMapEntity;

public class WallMapBlock
extends class_2237 {
    public static final MapCodec<WallMapBlock> CODEC = WallMapBlock.method_54094(WallMapBlock::new);
    public static final class_2754<class_2350> FACING = class_2741.field_12481;

    public WallMapBlock(class_4970.class_2251 settings) {
        super(settings);
        this.method_9590((class_2680)((class_2680)this.method_9595().method_11664()).method_11657(FACING, (Comparable)class_2350.field_11043));
    }

    protected MapCodec<? extends class_2237> method_53969() {
        return CODEC;
    }

    protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
        builder.method_11667(new class_2769[]{FACING});
    }

    public class_2680 method_9605(class_1750 c) {
        class_2350 face = c.method_8038();
        return (class_2680)this.method_9564().method_11657(FACING, (Comparable)(face.method_10166().method_10179() ? face : c.method_8042().method_10153()));
    }

    protected class_2464 method_9604(class_2680 state) {
        return class_2464.field_11458;
    }

    protected class_265 method_9530(class_2680 s, class_1922 w, class_2338 p, class_3726 c) {
        return switch ((class_2350)s.method_11654(FACING)) {
            case class_2350.field_11043 -> class_2248.method_9541((double)0.0, (double)0.0, (double)14.0, (double)16.0, (double)16.0, (double)16.0);
            case class_2350.field_11035 -> class_2248.method_9541((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)2.0);
            case class_2350.field_11034 -> class_2248.method_9541((double)0.0, (double)0.0, (double)0.0, (double)2.0, (double)16.0, (double)16.0);
            default -> class_2248.method_9541((double)14.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
        };
    }

    public class_2586 method_10123(class_2338 pos, class_2680 state) {
        return new WallMapEntity(pos, state);
    }
}

