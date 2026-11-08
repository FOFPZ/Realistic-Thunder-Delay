/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.fabricmc.fabric.api.event.player.UseBlockCallback
 *  net.fabricmc.fabric.api.event.player.UseItemCallback
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3419
 *  net.minecraft.class_437
 *  net.minecraft.class_746
 */
package ru.realisticthunder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_437;
import net.minecraft.class_746;
import ru.realisticthunder.MapCache;
import ru.realisticthunder.RadarScreen;
import ru.realisticthunder.RainSound;
import ru.realisticthunder.RoofDrops;
import ru.realisticthunder.SkyLightning;
import ru.realisticthunder.WallMapRenderer;
import ru.realisticthunder.WeatherMod;
import ru.realisticthunder.WeatherVisuals;

public class ThunderClient
implements ClientModInitializer {
    public static final List<Strike> strikes = new ArrayList<Strike>();
    private static final class_3414 THUNDER = class_3414.method_47908((class_2960)class_2960.method_60655((String)"realistic_thunder", (String)"thunder"));
    private static final class_3414 SKY_THUNDER = class_3414.method_47908((class_2960)class_2960.method_60655((String)"realistic_thunder", (String)"sky_thunder"));
    private static final List<Pending> pending = new ArrayList<Pending>();
    private static long tick;
    private static final RainSound[] rain;
    private static final Random RANDOM;
    private static int skyCountdown;
    private static int fakeId;

    public static long elapsedTicks() {
        return tick;
    }

    public static void schedule(class_1937 world, double x, double y, double z) {
        ThunderClient.schedule(world, x, y, z, false);
    }

    private static void schedule(class_1937 world, double x, double y, double z, boolean sky) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        strikes.add(new Strike(world, x, z, tick, sky));
        if (strikes.size() > 128) {
            strikes.remove(0);
        }
        double distance = Math.sqrt(client.field_1724.method_5649(x, y, z));
        pending.add(new Pending(world, x, y, z, tick + Math.max(0L, Math.round(distance / 343.0 * 20.0)), sky));
    }

    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MapCache.shutdown());
        BlockEntityRendererRegistry.register(WeatherMod.WALL_MAP_ENTITY, WallMapRenderer::new);
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.method_8608() && player.method_5998(hand).method_31574(WeatherMod.DETECTOR)) {
                String weather;
                Optional<Strike> nearby = strikes.stream().filter(s -> s.world() == world && !s.sky()).min(Comparator.comparingDouble(s -> Math.hypot(s.x() - player.method_23317(), s.z() - player.method_23321())));
                String string = world.method_8546() ? "\u0433\u0440\u043e\u0437\u0430" : (weather = world.method_8419() ? "\u0434\u043e\u0436\u0434\u044c" : "\u044f\u0441\u043d\u043e");
                if (nearby.isPresent()) {
                    Strike hit = nearby.get();
                    int distance = (int)Math.round(Math.hypot(hit.x() - player.method_23317(), hit.z() - player.method_23321()));
                    player.method_7353((class_2561)class_2561.method_43470((String)("\u0414\u0435\u0442\u0435\u043a\u0442\u043e\u0440: " + weather + "; \u0443\u0434\u0430\u0440 X " + (int)hit.x() + " Z " + (int)hit.z() + ", " + distance + " \u0431\u043b\u043e\u043a\u043e\u0432; " + (tick - hit.time()) / 20L + " \u0441\u0435\u043a. \u043d\u0430\u0437\u0430\u0434")), false);
                } else {
                    player.method_7353((class_2561)class_2561.method_43470((String)("\u0414\u0435\u0442\u0435\u043a\u0442\u043e\u0440: " + weather + "; \u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0445 \u0443\u0434\u0430\u0440\u043e\u0432 \u043f\u043e\u0431\u043b\u0438\u0437\u043e\u0441\u0442\u0438 \u043d\u0435\u0442")), false);
                }
                return class_1269.field_5812;
            }
            return class_1269.field_5811;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (hand == class_1268.field_5808 && world.method_8608() && world.method_8320(hit.method_17777()).method_27852(WeatherMod.RADAR)) {
                class_310.method_1551().method_1507((class_437)new RadarScreen(hit.method_17777()));
                return class_1269.field_5812;
            }
            return class_1269.field_5811;
        });
        WorldRenderEvents.BEFORE_ENTITIES.register(WeatherVisuals::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ++tick;
            strikes.removeIf(s -> s.world() != client.field_1687 || tick - s.time() > 3600L);
            WeatherVisuals.tick(client);
            MapCache.tick(client);
            RoofDrops.tick(client);
            if (client.field_1687 != null && client.field_1724 != null && client.field_1687.method_8419()) {
                for (int i = 0; i < rain.length; ++i) {
                    if (rain[i] != null && !rain[i].method_4793() && (tick % 80L != 0L || client.method_1483().method_4877((class_1113)rain[i]))) continue;
                    ThunderClient.rain[i] = new RainSound(client.field_1687, i);
                    client.method_1483().method_4873((class_1113)rain[i]);
                }
                if (client.field_1687.method_8546() && --skyCountdown <= 0) {
                    skyCountdown = 900 + RANDOM.nextInt(900);
                    class_746 p = client.field_1724;
                    if (RANDOM.nextFloat() < 0.75f) {
                        double angle = RANDOM.nextDouble() * Math.PI * 2.0;
                        double d = 45 + RANDOM.nextInt(26);
                        double x = p.method_23317() + Math.cos(angle) * d;
                        double z = p.method_23321() + Math.sin(angle) * d;
                        double y = p.method_23318() + 35.0 + (double)RANDOM.nextInt(15);
                        SkyLightning bolt = new SkyLightning(client.field_1687, fakeId--, x, y, z, RANDOM.nextInt());
                        client.field_1687.method_53875((class_1297)bolt);
                        ThunderClient.schedule((class_1937)client.field_1687, x, y, z, true);
                    }
                }
            } else {
                for (int i = 0; i < rain.length; ++i) {
                    ThunderClient.rain[i] = null;
                }
                skyCountdown = 100;
            }
            Iterator<Pending> it = pending.iterator();
            while (it.hasNext()) {
                Pending p = it.next();
                if (client.field_1687 != p.world || client.field_1724 == null) {
                    it.remove();
                    continue;
                }
                if (tick < p.due) continue;
                if (p.sky) {
                    client.method_1483().method_4873((class_1113)new class_1109(class_2960.method_60655((String)"realistic_thunder", (String)"sky_thunder"), class_3419.field_15252, 0.28f, 0.9f, class_1113.method_43221(), false, 0, class_1113.class_1114.field_5478, 0.0, 0.0, 0.0, true));
                } else {
                    client.field_1687.method_8486(p.x, p.y, p.z, THUNDER, class_3419.field_15252, 32.0f, 1.0f, false);
                }
                it.remove();
            }
        });
    }

    static {
        rain = new RainSound[3];
        RANDOM = new Random();
        skyCountdown = 100;
        fakeId = -1000000000;
    }

    public record Strike(class_1937 world, double x, double z, long time, boolean sky) {
    }

    private record Pending(class_1937 world, double x, double y, double z, long due, boolean sky) {
    }
}

