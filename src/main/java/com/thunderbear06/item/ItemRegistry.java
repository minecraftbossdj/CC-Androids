package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.EntityRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
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

    public static final Item ANDROID_SPAWN_EGG = registerEgg(EntityRegistry.ANDROID_ENTITY, 0xb2b2b2,0x8a8c8b, "android_spawn");
    public static final Item ANDROID_ADVANCED_SPAWN_EGG = registerEgg(EntityRegistry.ADVANCED_ANDROID_ENTITY, 0xb2b2b2,0xa5a333, "android_advanced_spawn");
    public static final Item ANDROID_COMMAND_SPAWN_EGG = registerEgg(EntityRegistry.COMMAND_ANDROID_ENTITY, 0xfc9e46,0x9b5c22, "android_command_spawn");
    public static final Item ANDROID_ROGUE_SPAWN_EGG = registerEgg(EntityRegistry.ROGUE_ANDROID_ENTITY, 0xf41818,0x9b2222, "android_rogue_spawn");

    public static final ItemGroup ANDROIDS_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.cc-androids.android_item_group"))
            .icon(() -> new ItemStack(ANDROID_CPU))
            .entries((displayContext, entries) -> getGroupEntries(entries))
            .build();

    private static Item registerEgg(EntityType<? extends MobEntity> entityType, int color1, int color2, String path) {
        SpawnEggItem eggItem = new SpawnEggItem(entityType, color1, color2, new FabricItemSettings());

        Registry.register(Registries.ITEM, new Identifier(CCAndroids.MOD_ID, path), eggItem);
        return eggItem;
    }

    private static void getGroupEntries(ItemGroup.Entries entries) {
        entries.add(COMPONENTS);
        entries.add(ANDROID_CPU);
        entries.add(REDSTONE_REACTOR);
        entries.add(ANDROID_FRAME);
        entries.add(ANDROID_SPAWN_EGG);
        entries.add(ANDROID_ADVANCED_SPAWN_EGG);
        entries.add(ANDROID_COMMAND_SPAWN_EGG);
        entries.add(ANDROID_ROGUE_SPAWN_EGG);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(CCAndroids.MOD_ID, "androids_item_group"), ANDROIDS_ITEM_GROUP);
    }

}
