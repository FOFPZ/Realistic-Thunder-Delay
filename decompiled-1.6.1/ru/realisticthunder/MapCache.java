/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.util.HexFormat
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_310
 *  net.minecraft.class_5218
 *  net.minecraft.class_638
 */
package ru.realisticthunder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2902;
import net.minecraft.class_310;
import net.minecraft.class_5218;
import net.minecraft.class_638;
import ru.realisticthunder.ThunderClient;

public class MapCache {
    private static final Map<Long, Integer> cells = new LinkedHashMap<Long, Integer>();
    private static class_638 world;
    private static final Random rand;
    private static int age;
    private static Path savePath;
    private static boolean dirty;
    private static final Map<class_2338, Saved> snapshots;

    private static void save() {
        if (savePath == null || !dirty) {
            return;
        }
        try {
            Files.createDirectories(savePath.getParent(), new FileAttribute[0]);
            Path temp = savePath.resolveSibling(String.valueOf(savePath.getFileName()) + ".tmp");
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temp, new OpenOption[0])));){
                out.writeInt(1414745393);
                out.writeInt(cells.size());
                for (Map.Entry<Long, Integer> e : cells.entrySet()) {
                    out.writeLong(e.getKey());
                    out.writeInt(e.getValue());
                }
            }
            Files.move(temp, savePath, StandardCopyOption.REPLACE_EXISTING);
            dirty = false;
        }
        catch (IOException error) {
            System.err.println("Thunder System: map save failed: " + String.valueOf(error));
        }
    }

    public static void shutdown() {
        MapCache.save();
    }

    private static void open(class_310 c) {
        savePath = null;
        if (c.method_1576() == null || c.field_1687 == null) {
            return;
        }
        String dim = c.field_1687.method_27983().method_29177().toString();
        String id = HexFormat.of().formatHex(MapCache.java_securityDigest(dim));
        savePath = c.method_1576().method_27050(class_5218.field_24188).resolve("data/thunder_system/map_" + id + ".dat");
        if (!Files.isRegularFile(savePath, new LinkOption[0])) {
            return;
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(savePath, new OpenOption[0])));){
            if (in.readInt() != 1414745393) {
                return;
            }
            int count = in.readInt();
            if (count < 0 || count > 30000) {
                return;
            }
            for (int i = 0; i < count; ++i) {
                cells.put(in.readLong(), in.readInt());
            }
        }
        catch (IOException error) {
            cells.clear();
            System.err.println("Thunder System: map load failed: " + String.valueOf(error));
        }
    }

    private static byte[] java_securityDigest(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    public static void tick(class_310 c) {
        if (c.field_1687 != world) {
            MapCache.save();
            world = c.field_1687;
            cells.clear();
            snapshots.clear();
            dirty = false;
            MapCache.open(c);
        }
        if (c.field_1687 == null || c.field_1724 == null || ++age % 4 != 0) {
            return;
        }
        int px = c.field_1724.method_31477() >> 4;
        int pz = c.field_1724.method_31479() >> 4;
        for (int i = 0; i < 32; ++i) {
            int color;
            int cz;
            int cx = px + rand.nextInt(21) - 10;
            if (!world.method_8393(cx, cz = pz + rand.nextInt(21) - 10)) continue;
            int x = cx * 16 + 8;
            int z = cz * 16 + 8;
            int y = world.method_8624(class_2902.class_2903.field_13197, x, z);
            class_2338 top = new class_2338(x, y, z);
            int n = color = !world.method_8316(top.method_10074()).method_15769() ? -13341547 : -11372724;
            if (world.method_70745(top) == class_1959.class_1963.field_9383) {
                color = -5060923;
            } else if (world.method_70745(top) == class_1959.class_1963.field_9384) {
                color = -4939414;
            }
            int shade = Math.max(-22, Math.min(28, (y - 64) / 2));
            int r = Math.max(0, Math.min(255, (color >> 16 & 0xFF) + shade));
            int g = Math.max(0, Math.min(255, (color >> 8 & 0xFF) + shade));
            int b = Math.max(0, Math.min(255, (color & 0xFF) + shade));
            long key = (long)cx << 32 | (long)cz & 0xFFFFFFFFL;
            int val = 0xFF000000 | r << 16 | g << 8 | b;
            Integer old = cells.put(key, val);
            if (old != null && old == val) continue;
            dirty = true;
        }
        while (cells.size() > 30000) {
            cells.remove(cells.keySet().iterator().next());
            dirty = true;
        }
        if (age % 400 == 0) {
            MapCache.save();
        }
    }

    public static int[] snapshot(class_2338 center) {
        Saved cached = snapshots.get(center);
        if (cached != null && age - cached.age < 20) {
            return cached.pixels;
        }
        int n = 64;
        int[] pixels = new int[n * n];
        Arrays.fill(pixels, -15325387);
        for (Map.Entry<Long, Integer> e : cells.entrySet()) {
            double dz;
            int cx = (int)(e.getKey() >> 32);
            int cz = (int)e.getKey().longValue();
            double dx = cx * 16 + 8 - center.method_10263();
            if (dx * dx + (dz = (double)(cz * 16 + 8 - center.method_10260())) * dz > 2250000.0) continue;
            int x = (int)Math.floor((dx + 1500.0) * (double)n / 3000.0);
            int z = (int)Math.floor((dz + 1500.0) * (double)n / 3000.0);
            if (x < 0 || x >= n || z < 0 || z >= n) continue;
            pixels[z * n + x] = e.getValue();
        }
        for (ThunderClient.Strike s : ThunderClient.strikes) {
            double dz;
            double dx;
            if (s.world() != world || (dx = s.x() - (double)center.method_10263()) * dx + (dz = s.z() - (double)center.method_10260()) * dz > 2250000.0) continue;
            int x = (int)((dx + 1500.0) * (double)n / 3000.0);
            int z = (int)((dz + 1500.0) * (double)n / 3000.0);
            if (x < 0 || x >= n || z < 0 || z >= n) continue;
            pixels[z * n + x] = s.sky() ? -751116 : -11456;
        }
        class_310 c = class_310.method_1551();
        if (c.field_1724 != null && world == c.field_1687) {
            int x = (int)((c.field_1724.method_23317() - (double)center.method_10263() + 1500.0) * (double)n / 3000.0);
            int z = (int)((c.field_1724.method_23321() - (double)center.method_10260() + 1500.0) * (double)n / 3000.0);
            if (x >= 0 && x < n && z >= 0 && z < n) {
                pixels[z * n + x] = -12189737;
            }
        }
        if (snapshots.size() > 32) {
            snapshots.clear();
        }
        snapshots.put(center.method_10062(), new Saved(age, pixels));
        return pixels;
    }

    static {
        rand = new Random();
        snapshots = new LinkedHashMap<class_2338, Saved>();
    }

    private record Saved(int age, int[] pixels) {
    }
}

