/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11659
 *  net.minecraft.class_11683$class_11792
 *  net.minecraft.class_11954
 *  net.minecraft.class_12075
 *  net.minecraft.class_12249
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_2586
 *  net.minecraft.class_4587
 *  net.minecraft.class_5614$class_5615
 *  net.minecraft.class_7833
 *  net.minecraft.class_827
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.realisticthunder;

import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_4587;
import net.minecraft.class_5614;
import net.minecraft.class_7833;
import net.minecraft.class_827;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.realisticthunder.MapCache;
import ru.realisticthunder.WallMapBlock;
import ru.realisticthunder.WallMapEntity;

public class WallMapRenderer
implements class_827<WallMapEntity, State> {
    public WallMapRenderer(class_5614.class_5615 ignored) {
    }

    public State createRenderState() {
        return new State();
    }

    public void updateRenderState(WallMapEntity be, State state, float delta, class_243 camera, class_11683.class_11792 overlay) {
        super.method_74331((class_2586)be, (class_11954)state, delta, camera, overlay);
        state.pixels = MapCache.snapshot(be.method_11016());
    }

    public void render(State state, class_4587 matrices, class_11659 queue, class_12075 camera) {
        if (state.pixels == null) {
            return;
        }
        float rot = switch ((class_2350)state.field_62674.method_11654(WallMapBlock.FACING)) {
            case class_2350.field_11034 -> 90.0f;
            case class_2350.field_11035 -> 180.0f;
            case class_2350.field_11039 -> 270.0f;
            default -> 0.0f;
        };
        matrices.method_22903();
        matrices.method_22904(0.5, 0.5, 0.5);
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-rot));
        matrices.method_22904(-0.5, -0.5, -0.5);
        int[] pixels = (int[])state.pixels.clone();
        queue.method_73483(matrices, class_12249.method_76003(), (entry, out) -> {
            Matrix4f matrix = entry.method_23761();
            float z = 0.873f;
            for (int row = 0; row < 64; ++row) {
                for (int col = 0; col < 64; ++col) {
                    int argb = pixels[row * 64 + col];
                    float r = (float)(argb >> 16 & 0xFF) / 255.0f;
                    float g = (float)(argb >> 8 & 0xFF) / 255.0f;
                    float b = (float)(argb & 0xFF) / 255.0f;
                    float x0 = 0.05f + 0.9f * (float)col / 64.0f;
                    float x1 = 0.05f + 0.9f * (float)(col + 1) / 64.0f;
                    float y1 = 0.95f - 0.9f * (float)row / 64.0f;
                    float y0 = 0.95f - 0.9f * (float)(row + 1) / 64.0f;
                    out.method_22918((Matrix4fc)matrix, x0, y0, z).method_22915(r, g, b, 1.0f);
                    out.method_22918((Matrix4fc)matrix, x0, y1, z).method_22915(r, g, b, 1.0f);
                    out.method_22918((Matrix4fc)matrix, x1, y1, z).method_22915(r, g, b, 1.0f);
                    out.method_22918((Matrix4fc)matrix, x1, y0, z).method_22915(r, g, b, 1.0f);
                }
            }
        });
        matrices.method_22909();
    }

    public static class State
    extends class_11954 {
        int[] pixels;
    }
}

