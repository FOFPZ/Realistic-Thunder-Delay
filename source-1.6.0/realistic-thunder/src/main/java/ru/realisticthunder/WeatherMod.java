package ru.realisticthunder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
public class WeatherMod implements ModInitializer {
    public static final Identifier ID=Identifier.of("realistic_thunder","storm_radar");
    public static Block RADAR;
    public static Item RADAR_ITEM;
    @Override public void onInitialize() {
        var bk=RegistryKey.of(RegistryKeys.BLOCK,ID);
        var ik=RegistryKey.of(RegistryKeys.ITEM,ID);
        RADAR=Registry.register(Registries.BLOCK,bk,new Block(AbstractBlock.Settings.create().registryKey(bk).strength(3.0f)));
        RADAR_ITEM=Registry.register(Registries.ITEM,ik,new BlockItem(RADAR,new Item.Settings().registryKey(ik)));
        RegistryKey<ItemGroup> group=RegistryKey.of(RegistryKeys.ITEM_GROUP,Identifier.of("minecraft","functional_blocks"));
        ItemGroupEvents.modifyEntriesEvent(group).register(entries->entries.add(RADAR_ITEM));
        Registry.register(Registries.ITEM_GROUP,Identifier.of("realistic_thunder","thunder_system"),
            FabricItemGroup.builder().displayName(Text.literal("thunder System"))
                .icon(()->new ItemStack(RADAR_ITEM)).entries((context,entries)->entries.add(RADAR_ITEM)).build());
    }
}
