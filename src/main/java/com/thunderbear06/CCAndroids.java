package com.thunderbear06;

import com.thunderbear06.computer.AndroidAPI;
import com.thunderbear06.computer.AndroidAccess;
import com.thunderbear06.computer.ComputerComponents;
import com.thunderbear06.entity.AndroidEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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

	public static final EntityType<AndroidEntity> ROBOT_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(CCAndroids.MOD_ID, "robot"),
			EntityType.Builder.create(AndroidEntity::new, SpawnGroup.MISC).build("robot")
	);

	@Override
	public void onInitialize() {
		ComputerCraftAPI.registerAPIFactory(computer -> {
			AndroidAccess android = computer.getComponent(ComputerComponents.ANDROID_COMPUTER);
			return android == null ? null : new AndroidAPI(android);
		});

		FabricDefaultAttributeRegistry.register(ROBOT_ENTITY, AndroidEntity.createAndroidAttributes());
	}
}