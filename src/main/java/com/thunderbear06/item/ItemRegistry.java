package com.thunderbear06.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ItemRegistry {

    public static final Item ANDROID_CORE = Registry.register(
            Registries.ITEM,
            "android_core",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static void register() {
        Registry.register(Registries.ITEM, "android_core", ANDROID_CORE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> itemGroup.add(ANDROID_CORE));
    }

}
