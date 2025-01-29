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