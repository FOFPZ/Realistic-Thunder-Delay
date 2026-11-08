/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_12249
 *  net.minecraft.class_1922
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.realisticthunder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_12249;
import net.minecraft.class_1922;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2902;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class WeatherVisuals {
    private static final ArrayList<Patch> patches = new ArrayList();
    private static final Random random = new Random();
    private static long tick;

    public static void tick(class_310 c) {
        ++tick;
        if (c.field_1687 == null || c.field_1724 == null) {
            patches.clear();
            return;
        }
        class_638 w = c.field_1687;
        class_746 player = c.field_1724;
        Iterator<Patch> it = patches.iterator();
        while (it.hasNext()) {
            Patch p2 = it.next();
            if (p2.world == w && p2.expiry >= tick && !(p2.floor.method_19770((class_2374)player.method_73189()) > 2025.0) && w.method_8320(p2.floor.method_10084()).method_45474()) continue;
            it.remove();
        }
        if (!w.method_8419()) {
            patches.removeIf(p -> p.expiry < tick + 400L);
            return;
        }
        for (int i = 0; i < 10; ++i) {
            int z;
            int x = player.method_31477() + random.nextInt(41) - 20;
            if (!w.method_8393(x >> 4, (z = player.method_31479() + random.nextInt(41) - 20) >> 4)) continue;
            int y = w.method_8624(class_2902.class_2903.field_13197, x, z);
            class_2338 surface = new class_2338(x, y, z);
            class_2338 floor = surface.method_10074();
            if (Math.abs((double)y - player.method_23318()) > 14.0 || w.method_70745(surface) != class_1959.class_1963.field_9382 || !w.method_8320(surface).method_45474() || !w.method_8320(floor).method_26234((class_1922)w, floor) || !w.method_8316(floor).method_15769()) continue;
            if (random.nextInt(4) == 0) {
                w.method_8406((class_2394)class_2398.field_11202, (double)x + random.nextDouble(), (double)y + 0.04, (double)z + random.nextDouble(), 0.0, 0.03, 0.0);
            }
            if (random.nextInt(5) == 0) {
                w.method_8406((class_2394)class_2398.field_18306, (double)x + random.nextDouble(), (double)(y + 2) + random.nextDouble() * 4.0, (double)z + random.nextDouble(), 0.0, -0.08, 0.0);
            }
            if (patches.size() >= 220 || random.nextInt(3) != 0 || !patches.stream().noneMatch(p -> p.floor.equals((Object)floor))) continue;
            patches.add(new Patch(w, floor, tick + 700L + (long)random.nextInt(500), 0.24f + random.nextFloat() * 0.22f));
        }
    }

    public static void render(WorldRenderContext context) {
        class_310 c = class_310.method_1551();
        if (c.field_1687 == null || patches.isEmpty() || context.consumers() == null || context.matrices() == null) {
            return;
        }
        class_243 camera = c.field_1773.method_19418().method_71156();
        List<Patch> snapshot = List.copyOf(patches);
        context.commandQueue().method_73483(context.matrices(), class_12249.method_76003(), (entry, out) -> {
            Matrix4f m = entry.method_23761();
            for (Patch p : snapshot) {
                int i;
                float x = (float)((double)p.floor.method_10263() + 0.5 - camera.field_1352);
                float y = (float)((double)p.floor.method_10264() + 1.012 - camera.field_1351);
                float z = (float)((double)p.floor.method_10260() + 0.5 - camera.field_1350);
                float a = p.size;
                float b = a * 0.73f;
                Random shape = new Random(p.floor.method_10063());
                float[] radius = new float[8];
                for (i = 0; i < 8; ++i) {
                    radius[i] = 0.72f + shape.nextFloat() * 0.28f;
                }
                for (i = 0; i < 8; ++i) {
                    double u = (double)i * Math.PI / 4.0;
                    double v = (double)((i + 1) % 8) * Math.PI / 4.0;
                    float x1 = x + (float)Math.cos(u) * a * radius[i];
                    float z1 = z + (float)Math.sin(u) * b * radius[i];
                    float x2 = x + (float)Math.cos(v) * a * radius[(i + 1) % 8];
                    float z2 = z + (float)Math.sin(v) * b * radius[(i + 1) % 8];
                    WeatherVisuals.quad(out, m, x, y, z, x1, y, z1, x2, y, z2, x, y, z);
                    WeatherVisuals.quad(out, m, x, y, z, x2, y, z2, x1, y, z1, x, y, z);
                }
                float h = y + 0.002f;
                WeatherVisuals.glint(out, m, x - a * 0.3f, h, z - b * 0.25f, x + a * 0.25f, h, z - b * 0.25f, x + a * 0.2f, h, z - b * 0.17f, x - a * 0.3f, h, z - b * 0.17f);
            }
        });
    }

    private static void glint(class_4588 v, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        v.method_22918((Matrix4fc)m, x4, y4, z4).method_22915(0.65f, 0.76f, 0.81f, 0.32f);
        v.method_22918((Matrix4fc)m, x3, y3, z3).method_22915(0.65f, 0.76f, 0.81f, 0.32f);
        v.method_22918((Matrix4fc)m, x2, y2, z2).method_22915(0.65f, 0.76f, 0.81f, 0.32f);
        v.method_22918((Matrix4fc)m, x1, y1, z1).method_22915(0.65f, 0.76f, 0.81f, 0.32f);
    }

    private static void quad(class_4588 v, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        v.method_22918((Matrix4fc)m, x1, y1, z1).method_22915(0.3f, 0.44f, 0.5f, 0.4f);
        v.method_22918((Matrix4fc)m, x2, y2, z2).method_22915(0.3f, 0.44f, 0.5f, 0.4f);
        v.method_22918((Matrix4fc)m, x3, y3, z3).method_22915(0.3f, 0.44f, 0.5f, 0.4f);
        v.method_22918((Matrix4fc)m, x4, y4, z4).method_22915(0.3f, 0.44f, 0.5f, 0.4f);
    }

    private record Patch(class_638 world, class_2338 floor, long expiry, float size) {
    }
}

