/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_638
 */
package ru.realisticthunder;

import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2902;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_638;
import ru.realisticthunder.ThunderClient;

public class RadarScreen
extends class_437 {
    private static final int N = 33;
    private final class_2338 center;
    private final int[][] colors = new int[33][33];
    private final int[][] heights = new int[33][33];
    private int refresh;
    private int zoom = 1;
    private static final int[] STEPS = new int[]{4, 8, 16, 32};

    public RadarScreen(class_2338 pos) {
        super((class_2561)class_2561.method_43470((String)"\u0413\u0420\u041e\u0417\u041e\u0412\u041e\u0419 \u0420\u0410\u0414\u0410\u0420"));
        this.center = pos.method_10062();
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25393() {
        if (this.field_22787 == null || this.field_22787.field_1687 == null || this.refresh++ % 20 != 0) {
            return;
        }
        class_638 w = this.field_22787.field_1687;
        int step = STEPS[this.zoom];
        for (int z = 0; z < 33; ++z) {
            for (int x = 0; x < 33; ++x) {
                int base;
                int y;
                int pz;
                int px = this.center.method_10263() + (x - 16) * step;
                if (!w.method_8393(px >> 4, (pz = this.center.method_10260() + (z - 16) * step) >> 4)) {
                    this.colors[x][z] = -14669773;
                    this.heights[x][z] = Integer.MIN_VALUE;
                    continue;
                }
                this.heights[x][z] = y = w.method_8624(class_2902.class_2903.field_13197, px, pz);
                class_2338 p = new class_2338(px, y, pz);
                class_1959.class_1963 type = w.method_70745(p);
                boolean water = !w.method_8316(p.method_10074()).method_15769();
                int n = base = water ? -14397848 : -11638961;
                if (w.method_8419() && type == class_1959.class_1963.field_9382) {
                    base = w.method_8546() ? -10006399 : -13275247;
                } else if (w.method_8419() && type == class_1959.class_1963.field_9383) {
                    base = -6047804;
                } else if (type == class_1959.class_1963.field_9384 && !water) {
                    base = -7766955;
                }
                int shade = Math.max(-20, Math.min(20, (y - 64) / 2));
                this.colors[x][z] = this.shade(base, shade);
            }
        }
    }

    private int shade(int color, int amount) {
        int r = Math.max(0, Math.min(255, (color >> 16 & 0xFF) + amount));
        int g = Math.max(0, Math.min(255, (color >> 8 & 0xFF) + amount));
        int b = Math.max(0, Math.min(255, (color & 0xFF) + amount));
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    protected void method_25426() {
        this.refresh = 0;
        this.method_25393();
    }

    public boolean method_25401(double x, double y, double horizontal, double vertical) {
        int newZoom = Math.max(0, Math.min(STEPS.length - 1, this.zoom + (vertical > 0.0 ? -1 : 1)));
        if (newZoom != this.zoom) {
            this.zoom = newZoom;
            this.refresh = 0;
            this.method_25393();
        }
        return true;
    }

    public void method_25394(class_332 c, int mouseX, int mouseY, float delta) {
        c.method_25294(0, 0, this.field_22789, this.field_22790, -535816930);
        int cell = Math.max(3, Math.min(9, Math.min((this.field_22789 - 36) / 33, (this.field_22790 - 110) / 33)));
        int size = 33 * cell;
        int l = (this.field_22789 - size) / 2;
        int top = Math.max(26, (this.field_22790 - size - 56) / 2);
        int cx = l + size / 2;
        int cy = top + size / 2;
        int step = STEPS[this.zoom];
        c.method_25294(l - 3, top - 3, l + size + 3, top + size + 3, -10980229);
        c.method_25294(l - 2, top - 2, l + size + 2, top + size + 2, -15589847);
        c.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, top - 20, -2363404);
        for (int z = 0; z < 33; ++z) {
            for (int x = 0; x < 33; ++x) {
                c.method_25294(l + x * cell, top + z * cell, l + (x + 1) * cell, top + (z + 1) * cell, this.colors[x][z]);
            }
        }
        for (int i = 0; i <= 33; i += 8) {
            c.method_25294(l + i * cell, top, l + i * cell + 1, top + size, 1345472341);
            c.method_25294(l, top + i * cell, l + size, top + i * cell + 1, 1345472341);
        }
        c.method_25294(cx - 3, cy - 1, cx + 4, cy + 2, -1);
        c.method_25294(cx - 1, cy - 3, cx + 2, cy + 4, -1);
        int ground = 0;
        int sky = 0;
        String hoverStrike = null;
        if (this.field_22787 != null && this.field_22787.field_1687 != null) {
            for (ThunderClient.Strike s : ThunderClient.strikes) {
                if (s.world() != this.field_22787.field_1687) continue;
                int px = cx + (int)Math.round((s.x() - (double)this.center.method_10263()) * (double)cell / (double)step);
                int py = cy + (int)Math.round((s.z() - (double)this.center.method_10260()) * (double)cell / (double)step);
                if (px < l || px >= l + size || py < top || py >= top + size) continue;
                if (s.sky()) {
                    ++sky;
                } else {
                    ++ground;
                }
                if (Math.abs(mouseX - px) <= 5 && Math.abs(mouseY - py) <= 5) {
                    long seconds = Math.max(0L, (ThunderClient.elapsedTicks() - s.time()) / 20L);
                    hoverStrike = (s.sky() ? "\u041d\u0435\u0431\u043e" : "\u0423\u0434\u0430\u0440") + " X " + (int)s.x() + " Z " + (int)s.z() + " \u00b7 " + seconds + " \u0441 \u043d\u0430\u0437\u0430\u0434";
                }
                int color = s.sky() ? -1143561 : -11204;
                c.method_25294(px - 3, py - 3, px + 4, py + 4, -15393756);
                c.method_25294(px - 2, py - 2, px + 3, py + 3, color);
            }
            if (this.field_22787.field_1724 != null) {
                int px = cx + (int)Math.round((this.field_22787.field_1724.method_23317() - (double)this.center.method_10263()) * (double)cell / (double)step);
                int py = cy + (int)Math.round((this.field_22787.field_1724.method_23321() - (double)this.center.method_10260()) * (double)cell / (double)step);
                if (px >= l && px < l + size && py >= top && py < top + size) {
                    c.method_25294(px - 2, py - 2, px + 3, py + 3, -13434923);
                }
            }
        }
        c.method_25300(this.field_22793, "\u0421", this.field_22789 / 2, top - 10, -3614756);
        String weather = this.field_22787 != null && this.field_22787.field_1687 != null ? (this.field_22787.field_1687.method_8546() ? "\u0413\u0420\u041e\u0417\u0410" : (this.field_22787.field_1687.method_8419() ? "\u0414\u041e\u0416\u0414\u042c" : "\u042f\u0421\u041d\u041e")) : "\u041d\u0415\u0422 \u0414\u0410\u041d\u041d\u042b\u0425";
        c.method_25303(this.field_22793, weather, l, top + size + 6, -2364174);
        c.method_25303(this.field_22793, "\u0423\u0434\u0430\u0440\u044b: " + ground + "  \u041d\u0435\u0431\u043e: " + sky, l, top + size + 18, -8061);
        int gx = (mouseX - l) / cell;
        int gz = (mouseY - top) / cell;
        String info = "\u041a\u043e\u043b\u0435\u0441\u043e: \u043c\u0430\u0441\u0448\u0442\u0430\u0431 \u00b7 \u043a\u043b\u0435\u0442\u043a\u0430 " + step + " \u0431\u043b.";
        if (mouseX >= l && mouseX < l + size && mouseY >= top && mouseY < top + size && gx >= 0 && gx < 33 && gz >= 0 && gz < 33) {
            int wx = this.center.method_10263() + (gx - 16) * step;
            int wz = this.center.method_10260() + (gz - 16) * step;
            info = "X " + wx + "  Z " + wz + (String)(this.heights[gx][gz] == Integer.MIN_VALUE ? " \u00b7 \u043d\u0435\u0442 \u0434\u0430\u043d\u043d\u044b\u0445" : "  Y " + this.heights[gx][gz]);
        }
        if (hoverStrike != null) {
            info = hoverStrike;
        }
        c.method_25300(this.field_22793, info, this.field_22789 / 2, top + size + 30, -5651252);
        c.method_25303(this.field_22793, "\u0417", l - 12, cy - 4, -5651252);
        c.method_25303(this.field_22793, "\u0412", l + size + 4, cy - 4, -5651252);
        c.method_25303(this.field_22793, "\u0416\u0451\u043b\u0442\u044b\u0439: \u0443\u0434\u0430\u0440  \u0420\u043e\u0437\u043e\u0432\u044b\u0439: \u043d\u0435\u0431\u043e  \u0411\u0438\u0440\u044e\u0437\u043e\u0432\u044b\u0439: \u0438\u0433\u0440\u043e\u043a", l, top + size + 42, -5651252);
    }
}

