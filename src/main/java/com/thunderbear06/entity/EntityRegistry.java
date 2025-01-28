package com.thunderbear06.entity;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.android.RogueDroidEntity;
import com.thunderbear06.entity.android.UnfinishedAndroidEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityRegistry {
    public static final EntityType<AndroidEntity> ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "android"),
            EntityType.Builder.create(AndroidEntity::new, SpawnGroup.MISC).build("android")
    );

    public static final EntityType<RogueDroidEntity> ROGUE_ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "rogue_android"),
            EntityType.Builder.create(RogueDroidEntity::new, SpawnGroup.MISC).build("rogue_android")
    );
    public static final EntityType<UnfinishedAndroidEntity> UNFINISHED_ANDROID_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(CCAndroids.MOD_ID, "unfinished_android"),
            EntityType.Builder.create(UnfinishedAndroidEntity::new, SpawnGroup.MISC).build("unfinished_android")
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(ANDROID_ENTITY, BaseAndroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(ROGUE_ANDROID_ENTITY, BaseAndroidEntity.createAndroidAttributes());
        FabricDefaultAttributeRegistry.register(UNFINISHED_ANDROID_ENTITY, BaseAndroidEntity.createAndroidAttributes());
    }
}
