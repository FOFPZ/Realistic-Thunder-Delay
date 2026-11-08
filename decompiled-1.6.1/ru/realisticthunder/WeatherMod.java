/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
 *  net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
 *  net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1792$class_1793
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2248
 *  net.minecraft.class_2378
 *  net.minecraft.class_2561
 *  net.minecraft.class_2591
 *  net.minecraft.class_2960
 *  net.minecraft.class_4970$class_2251
 *  net.minecraft.class_5321
 *  net.minecraft.class_7923
 *  net.minecraft.class_7924
 */
package ru.realisticthunder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import ru.realisticthunder.StormCharm;
import ru.realisticthunder.WallMapBlock;
import ru.realisticthunder.WallMapEntity;

public class WeatherMod
implements ModInitializer {
    public static class_2248 RADAR;
    public static class_2248 WALL_MAP;
    public static class_1792 RADAR_ITEM;
    public static class_1792 WALL_MAP_ITEM;
    public static class_1792 DETECTOR;
    public static class_1792 CHARM;
    public static class_2591<WallMapEntity> WALL_MAP_ENTITY;

    private static class_2960 id(String id) {
        return class_2960.method_60655((String)"realistic_thunder", (String)id);
    }

    private static class_1792.class_1793 itemSettings(String id) {
        return new class_1792.class_1793().method_63686(class_5321.method_29179((class_5321)class_7924.field_41197, (class_2960)WeatherMod.id(id)));
    }

    public void onInitialize() {
        class_5321 radarKey = class_5321.method_29179((class_5321)class_7924.field_41254, (class_2960)WeatherMod.id("storm_radar"));
        RADAR = (class_2248)class_2378.method_39197((class_2378)class_7923.field_41175, (class_5321)radarKey, (Object)new class_2248(class_4970.class_2251.method_9637().method_63500(radarKey).method_9632(3.0f)));
        RADAR_ITEM = (class_1792)class_2378.method_10230((class_2378)class_7923.field_41178, (class_2960)WeatherMod.id("storm_radar"), (Object)new class_1747(RADAR, WeatherMod.itemSettings("storm_radar")));
        class_5321 mapKey = class_5321.method_29179((class_5321)class_7924.field_41254, (class_2960)WeatherMod.id("wall_weather_map"));
        WALL_MAP = (class_2248)class_2378.method_39197((class_2378)class_7923.field_41175, (class_5321)mapKey, (Object)((Object)new WallMapBlock(class_4970.class_2251.method_9637().method_63500(mapKey).method_9632(1.0f).method_9634().method_22488())));
        WALL_MAP_ITEM = (class_1792)class_2378.method_10230((class_2378)class_7923.field_41178, (class_2960)WeatherMod.id("wall_weather_map"), (Object)new class_1747(WALL_MAP, WeatherMod.itemSettings("wall_weather_map")));
        WALL_MAP_ENTITY = (class_2591)class_2378.method_10230((class_2378)class_7923.field_41181, (class_2960)WeatherMod.id("wall_weather_map"), (Object)FabricBlockEntityTypeBuilder.create(WallMapEntity::new, (class_2248[])new class_2248[]{WALL_MAP}).build());
        DETECTOR = (class_1792)class_2378.method_10230((class_2378)class_7923.field_41178, (class_2960)WeatherMod.id("storm_detector"), (Object)new class_1792(WeatherMod.itemSettings("storm_detector").method_7889(1)));
        CHARM = (class_1792)class_2378.method_10230((class_2378)class_7923.field_41178, (class_2960)WeatherMod.id("storm_charm"), (Object)((Object)new StormCharm(WeatherMod.itemSettings("storm_charm").method_7889(1))));
        class_5321 group = class_5321.method_29179((class_5321)class_7924.field_44688, (class_2960)class_2960.method_60655((String)"minecraft", (String)"functional_blocks"));
        ItemGroupEvents.modifyEntriesEvent((class_5321)group).register(entries -> entries.method_45421((class_1935)RADAR_ITEM));
        class_2378.method_10230((class_2378)class_7923.field_44687, (class_2960)WeatherMod.id("thunder_system"), (Object)FabricItemGroup.builder().method_47321((class_2561)class_2561.method_43470((String)"thunder System")).method_47320(() -> new class_1799((class_1935)RADAR_ITEM)).method_47317((context, entries) -> {
            entries.method_45421((class_1935)RADAR_ITEM);
            entries.method_45421((class_1935)WALL_MAP_ITEM);
            entries.method_45421((class_1935)DETECTOR);
            entries.method_45421((class_1935)CHARM);
        }).method_47324());
    }
}

