/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10041
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_12249
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_919
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.realisticthunder.mixin;

import java.util.Random;
import net.minecraft.class_10041;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_919;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_919.class})
public class LightningRendererMixin {
    @Inject(method={"method_4034(Lnet/minecraft/class_10041;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderBolt(class_10041 state, class_4587 matrices, class_11659 queue, class_12075 camera, CallbackInfo ci) {
        long seed = state.field_53445;
        queue.method_73483(matrices, class_12249.method_76003(), (entry, out) -> LightningRendererMixin.draw(entry.method_23761(), out, seed));
        ci.cancel();
    }

    private static void draw(Matrix4f m, class_4588 out, long seed) {
        Random r = new Random(seed);
        boolean sky = (seed & 0xFFFFFFFF00000000L) == 6491869649456594944L;
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        float dx = 0.0f;
        float dz = 0.0f;
        float dir = (float)(r.nextDouble() * Math.PI * 2.0);
        int count = sky ? 42 : 64;
        for (int i = 0; i < count; ++i) {
            float ny;
            float nz;
            float nx;
            if (sky) {
                dx = dx * 0.68f + (r.nextFloat() - 0.5f) * 0.95f;
                dz = dz * 0.68f + (r.nextFloat() - 0.5f) * 0.95f;
                nx = x + (float)Math.cos(dir) * 2.7f + dx;
                nz = z + (float)Math.sin(dir) * 2.7f + dz;
                ny = y + (r.nextFloat() - 0.5f) * 1.8f;
            } else {
                dx = dx * 0.68f + (r.nextFloat() - 0.5f) * 1.15f;
                dz = dz * 0.68f + (r.nextFloat() - 0.5f) * 1.15f;
                nx = x + dx + (r.nextFloat() - 0.5f) * 0.7f;
                nz = z + dz + (r.nextFloat() - 0.5f) * 0.7f;
                ny = y + 2.0f;
            }
            float taper = 0.5f + 0.5f * (float)i / (float)count;
            float w = (sky ? 0.07f : 0.09f) * taper;
            LightningRendererMixin.segment(m, out, x, y, z, nx, ny, nz, w * 4.0f, w * 3.0f, 0.12f);
            LightningRendererMixin.segment(m, out, x, y, z, nx, ny, nz, w, w * 0.8f, 0.94f);
            if (i > 4 && i < count - 3) {
                float f = r.nextFloat();
                float f2 = sky ? 0.24f : 0.2f;
                if (f < f2) {
                    float angle = (float)(r.nextDouble() * Math.PI * 2.0);
                    LightningRendererMixin.branch(m, out, r, nx, ny, nz, (float)Math.cos(angle), (float)Math.sin(angle), sky, 2 + r.nextInt(6), w * 0.65f);
                }
            }
            x = nx;
            y = ny;
            z = nz;
        }
    }

    private static void branch(Matrix4f m, class_4588 v, Random r, float x, float y, float z, float dx, float dz, boolean sky, int n, float w) {
        float speed = sky ? 2.1f : 1.0f;
        for (int i = 0; i < n; ++i) {
            float nx = x + dx * speed + (r.nextFloat() - 0.5f) * 0.85f;
            float nz = z + dz * speed + (r.nextFloat() - 0.5f) * 0.85f;
            float ny = y + (sky ? (r.nextFloat() - 0.5f) * 1.2f : -1.7f - r.nextFloat());
            float a = w * (1.0f - (float)i / (float)n);
            float b = w * (1.0f - (float)(i + 1) / (float)n);
            LightningRendererMixin.segment(m, v, x, y, z, nx, ny, nz, a, b, 0.78f);
            x = nx;
            y = ny;
            z = nz;
        }
    }

    private static void segment(Matrix4f m, class_4588 v, float x, float y, float z, float a, float b, float c, float w0, float w1, float alpha) {
        LightningRendererMixin.quad(m, v, x - w0, y, z, x + w0, y, z, a + w1, b, c, a - w1, b, c, alpha);
        LightningRendererMixin.quad(m, v, x, y - w0, z, x, y + w0, z, a, b + w1, c, a, b - w1, c, alpha);
        LightningRendererMixin.quad(m, v, x, y, z - w0, x, y, z + w0, a, b, c + w1, a, b, c - w1, alpha);
    }

    private static void quad(Matrix4f m, class_4588 v, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float alpha) {
        v.method_22918((Matrix4fc)m, x1, y1, z1).method_22915(0.92f, 0.96f, 1.0f, alpha);
        v.method_22918((Matrix4fc)m, x2, y2, z2).method_22915(0.92f, 0.96f, 1.0f, alpha);
        v.method_22918((Matrix4fc)m, x3, y3, z3).method_22915(0.92f, 0.96f, 1.0f, alpha);
        v.method_22918((Matrix4fc)m, x4, y4, z4).method_22915(0.92f, 0.96f, 1.0f, alpha);
    }
}

