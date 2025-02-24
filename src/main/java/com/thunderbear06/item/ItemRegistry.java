package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry {

    public static final Item COMPONENTS = Registry.register(
            Registries.ITEM,
            new Identifier(CCAndroids.MOD_ID, "components"),
            new Item(new FabricItemSettings())
    );

    public static final Item ANDROID_CPU = Registry.register(
            Registries.ITEM,
            new Identifier(CCAndroids.MOD_ID,"android_cpu"),
            new Item(new FabricItemSettings())
    );
    public static final Item REDSTONE_REACTOR = Registry.register(
            Registries.ITEM,
            new Identifier(CCAndroids.MOD_ID,"redstone_reactor"),
            new Item(new FabricItemSettings())
    );
    public static final Item ANDROID_FRAME = Registry.register(
            Registries.ITEM,
            new Identifier(CCAndroids.MOD_ID,"android_frame"),
            new AndroidFrameItem(new FabricItemSettings())
    );

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> {
            itemGroup.add(COMPONENTS);
            itemGroup.add(ANDROID_CPU);
            itemGroup.add(REDSTONE_REACTOR);
            itemGroup.add(ANDROID_FRAME);
        });
    }

}
