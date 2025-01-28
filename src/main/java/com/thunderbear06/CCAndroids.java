package com.thunderbear06;

import com.thunderbear06.computer.AndroidAPI;
import com.thunderbear06.computer.IAndroidAccess;
import com.thunderbear06.computer.ComputerComponents;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.RogueDroidEntity;
import com.thunderbear06.entity.android.UnfinishedAndroidEntity;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCAndroids implements ModInitializer {
	public static final String MOD_ID = "cc-androids";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

	@Override
	public void onInitialize() {
		ComputerCraftAPI.registerAPIFactory(computer -> {
			IAndroidAccess android = computer.getComponent(ComputerComponents.ANDROID_COMPUTER);
			return android == null ? null : new AndroidAPI(android);
		});

		ItemRegistry.register();
		EntityRegistry.register();
	}
}